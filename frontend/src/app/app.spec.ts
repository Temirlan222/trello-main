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

  it('показывает доску из ответа сервера', () => {
    const board = { id: 1, name: 'Демо', lists: [] };

    fixture.detectChanges();
    http.expectOne({method:'GET',url:'/api/boards'}).flush([board])
    // 3. Обнови HTML и проверь текст кнопки доски.
    fixture.detectChanges();
    const page: HTMLElement = fixture.nativeElement;
    const boardButton = page.querySelector('.board-name');
    const title = boardButton?.textContent?.trim();
    expect(title).toBe('Демо');
  });
  it('ошибка загрузки досок', () => {
    fixture.detectChanges();
    const request = http.expectOne({method:'GET',url:'/api/boards'});
    request.flush(null,{ status:500,statusText:'ServerError'});
    fixture.detectChanges();
    const page: HTMLElement = fixture.nativeElement;
    const boardButton = page.querySelector('[role="alert"]');
    const title = boardButton?.textContent;
    expect(title).toContain('Не удалось загрузить доски');
    });
  it('оставляет карточку в старой колонке и показывает ошибку переноса', () => {
    const card = {
      id: 42,
      title: 'Написать тесты',
      description: '',
      priority: 'MEDIUM',
      dueDate: null,
    };

    const board = {
      id: 1,
      name: 'Демо',
      lists: [
        { id: 10, name: 'В работе', isDone: false, cards: [card] },
        { id: 11, name: 'Готово', isDone: true, cards: [] },
      ],
    };

    fixture.detectChanges();
    http.expectOne({ method: 'GET', url: '/api/boards' }).flush([board]);
    fixture.componentInstance.toggleBoard(1);
    fixture.componentInstance.toggleCard(42);
    fixture.componentInstance.moveCard(42, '11');
    const request = http.expectOne({
      method: 'PATCH',
      url: '/api/cards/42/move',
    });
    expect(request.request.body).toEqual({ taskListId: 11 });
    request.flush(null, { status: 500, statusText: 'Server Error' });

    fixture.detectChanges();
    const page: HTMLElement = fixture.nativeElement;
    const columns = page.querySelectorAll('.task-list');
    expect(columns.length).toBe(2);

    const source = columns[0];
    const target = columns[1];

    const cardTitle = source.querySelector('.task-card button')?.textContent?.trim();
    expect(cardTitle).toBe(card.title);
    expect(target.querySelector('.task-card')).toBeNull();

    const errorMessage = source.querySelector('.task-card [role="alert"]')?.textContent;
    expect(errorMessage).toContain('Не удалось перенести карточку');
    http.expectNone({ method: 'GET', url: '/api/boards' });
    http.verify();
  });

});
