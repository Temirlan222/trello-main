import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  let fixture: ComponentFixture<App>;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    fixture = TestBed.createComponent(App);
    http = TestBed.inject(HttpTestingController);
  });

  it('отправляет данные карточки и обновляет доски после ответа', () => {
    const demoBoard = {
      id: 1,
      name: 'Демо',
      lists: [{ id: 10, name: 'В работе', isDone: false, cards: [] }],
    };

    fixture.detectChanges();
    http.expectOne({ method: 'GET', url: '/api/boards' }).flush([demoBoard]);
    fixture.componentInstance.toggleBoard(1);

    fixture.componentInstance.addCard('  Написать тесты  ', 10);

    const request = http.expectOne({
      method: 'POST',
      url: '/api/cards',
    });

    expect(request.request.body).toEqual({
      title: 'Написать тесты',
      taskListId: 10,
    });

    http.expectNone({ method: 'GET', url: '/api/boards' });

    request.flush(null, { status: 201, statusText: 'Created' });

    const reload = http.expectOne({
      method: 'GET',
      url: '/api/boards',
    });

    const newCard = {
      title: 'Написать тесты',
      id: 42,
      description: '',
      priority: 'MEDIUM',
      dueDate: null,
    };

    const updateBoard = {
      id: 1,
      name: 'Демо',
      lists: [{ id: 10, name: 'В работе', isDone: false, cards: [newCard] }],
    };
    reload.flush([updateBoard]);

    fixture.detectChanges();
    const page: HTMLElement = fixture.nativeElement;
    const column = page.querySelector('.task-list');
    const cardButton = column?.querySelector('.task-card button');
    const title = cardButton?.textContent?.trim();
    expect(title).toBe('Написать тесты');

    http.verify();
  });
  it('не отправляет запрос для названия из одних пробелов', () => {
    fixture.componentInstance.addCard('    ',10);
    http.expectNone({method:'POST', url:'/api/cards'});



    });
});
