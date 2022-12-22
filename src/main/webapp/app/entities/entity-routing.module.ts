import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'genus',
        data: { pageTitle: 'mycoNotesApp.genus.home.title' },
        loadChildren: () => import('./genus/genus.module').then(m => m.GenusModule),
      },
      {
        path: 'species',
        data: { pageTitle: 'mycoNotesApp.species.home.title' },
        loadChildren: () => import('./species/species.module').then(m => m.SpeciesModule),
      },
      {
        path: 'strain',
        data: { pageTitle: 'mycoNotesApp.strain.home.title' },
        loadChildren: () => import('./strain/strain.module').then(m => m.StrainModule),
      },
      {
        path: 'experiment',
        data: { pageTitle: 'mycoNotesApp.experiment.home.title' },
        loadChildren: () => import('./experiment/experiment.module').then(m => m.ExperimentModule),
      },
      {
        path: 'tek',
        data: { pageTitle: 'mycoNotesApp.tek.home.title' },
        loadChildren: () => import('./tek/tek.module').then(m => m.TekModule),
      },
      {
        path: 'instruction',
        data: { pageTitle: 'mycoNotesApp.instruction.home.title' },
        loadChildren: () => import('./instruction/instruction.module').then(m => m.InstructionModule),
      },
      {
        path: 'observation',
        data: { pageTitle: 'mycoNotesApp.observation.home.title' },
        loadChildren: () => import('./observation/observation.module').then(m => m.ObservationModule),
      },
      {
        path: 'image',
        data: { pageTitle: 'mycoNotesApp.image.home.title' },
        loadChildren: () => import('./image/image.module').then(m => m.ImageModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
