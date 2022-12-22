import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IInstruction, NewInstruction } from '../instruction.model';

export type PartialUpdateInstruction = Partial<IInstruction> & Pick<IInstruction, 'id'>;

export type EntityResponseType = HttpResponse<IInstruction>;
export type EntityArrayResponseType = HttpResponse<IInstruction[]>;

@Injectable({ providedIn: 'root' })
export class InstructionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/instructions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/instructions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(instruction: NewInstruction): Observable<EntityResponseType> {
    return this.http.post<IInstruction>(this.resourceUrl, instruction, { observe: 'response' });
  }

  update(instruction: IInstruction): Observable<EntityResponseType> {
    return this.http.put<IInstruction>(`${this.resourceUrl}/${this.getInstructionIdentifier(instruction)}`, instruction, {
      observe: 'response',
    });
  }

  partialUpdate(instruction: PartialUpdateInstruction): Observable<EntityResponseType> {
    return this.http.patch<IInstruction>(`${this.resourceUrl}/${this.getInstructionIdentifier(instruction)}`, instruction, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IInstruction>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInstruction[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInstruction[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getInstructionIdentifier(instruction: Pick<IInstruction, 'id'>): number {
    return instruction.id;
  }

  compareInstruction(o1: Pick<IInstruction, 'id'> | null, o2: Pick<IInstruction, 'id'> | null): boolean {
    return o1 && o2 ? this.getInstructionIdentifier(o1) === this.getInstructionIdentifier(o2) : o1 === o2;
  }

  addInstructionToCollectionIfMissing<Type extends Pick<IInstruction, 'id'>>(
    instructionCollection: Type[],
    ...instructionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const instructions: Type[] = instructionsToCheck.filter(isPresent);
    if (instructions.length > 0) {
      const instructionCollectionIdentifiers = instructionCollection.map(
        instructionItem => this.getInstructionIdentifier(instructionItem)!
      );
      const instructionsToAdd = instructions.filter(instructionItem => {
        const instructionIdentifier = this.getInstructionIdentifier(instructionItem);
        if (instructionCollectionIdentifiers.includes(instructionIdentifier)) {
          return false;
        }
        instructionCollectionIdentifiers.push(instructionIdentifier);
        return true;
      });
      return [...instructionsToAdd, ...instructionCollection];
    }
    return instructionCollection;
  }
}
