import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

import { AuthService } from '../../core/auth/auth.service';

interface LandingFeature {
  icon: string;
  title: string;
  description: string;
  imageUrl: string;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatCardModule, MatIconModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss'
})
export class LandingComponent {
  private readonly authService = inject(AuthService);

  readonly isAuthenticated = this.authService.isAuthenticated;

  readonly features: LandingFeature[] = [
    {
      icon: 'map',
      title: 'Itinerários sem esforço',
      description: 'Organize cada dia da sua viagem — atividades, horários e paradas — em um só lugar.',
      imageUrl:
        'https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=800&q=80'
    },
    {
      icon: 'payments',
      title: 'Orçamento sob controle',
      description: 'Acompanhe despesas e mantenha sua viagem dentro do orçamento planejado, sem surpresas.',
      imageUrl:
        'https://images.unsplash.com/photo-1500835556837-99ac94a94552?auto=format&fit=crop&w=800&q=80'
    },
    {
      icon: 'travel_explore',
      title: 'Clima, rotas e câmbio',
      description: 'Previsão do tempo, distâncias e conversão de moeda integrados ao seu planejamento.',
      imageUrl:
        'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=800&q=80'
    }
  ];
}
