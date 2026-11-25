import { Component, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
interface Card {
  id: number;
  title: string;
  priority: string;
  dueDate: string;
  description: string;
}
interface FocusCard {
  id: number;
  title: string;
  priority: string;
  score: number;
  reason: string;
}
interface TaskList {
  id: number;
  name: string;
  cards: Card[];
  isDone: boolean;
}

interface Board {
  id: number;
  name: string;
  lists: TaskList[];
}

@Component({
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('frontend');
  private readonly http = inject(HttpClient);
  loadError = signal(false);
  isLoading = signal(true);

  focusCards = signal<FocusCard[]>([]);
  focusedBoardId = signal<number | null>(null);
  focusErrorBoardId = signal<number | null>(null);
  focusLoadingBoardId = signal<number | null>(null);

  focusDeadlineLabel(reason: string) {
    const deadline = reason.split('; deadline: ')[1];
    switch (deadline) {
      case 'overdue': return 'Срок просрочен';
      case 'in 0-2 days': return 'До срока не больше двух дней';
      case 'in 3-7 days': return 'До срока от трёх до семи дней';
      case 'later': return 'До срока больше недели';
      case 'none': return 'Срок не задан';
      default: return 'Нет данных о сроке';
    }
  }

  loadFocus(boardId: number) {
    if (this.focusLoadingBoardId() !== null) {
      return;
    }

    if (this.focusedBoardId() === boardId) {
      this.focusedBoardId.set(null);
      return;
    }

    this.focusErrorBoardId.set(null);
    this.focusLoadingBoardId.set(boardId);

    this.http.get<FocusCard[]>(`/api/boards/${boardId}/focus`).subscribe({
      next: (cards) => {
        this.focusCards.set(cards);
        this.focusedBoardId.set(boardId);
        this.focusLoadingBoardId.set(null);
      },
      error: () => {
        this.focusCards.set([]);
        this.focusedBoardId.set(null);
        this.focusErrorBoardId.set(boardId);
        this.focusLoadingBoardId.set(null);
      },
    });
  }

  boards = signal<Board[]>([]);
  ngOnInit() {
    this.loadBoards();
  }

  private loadBoards() {
    this.http.get<Board[]>('/api/boards').subscribe({
      next: (serverBoards) => {
        this.boards.set(serverBoards);
        this.loadError.set(false);
        this.isLoading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  openedBoardId: number | null = null;
  boardActionsId: number | null = null;
  boardDeleteErrorId = signal<number | null>(null);

  toggleBoardActions(id: number) {
    if (this.boardActionsId === id) {
      this.boardActionsId = null;
    } else {
      this.boardActionsId = id;
    }
  }

  toggleBoard(id: number) {
    if (this.openedBoardId === id) {
      this.openedBoardId = null;
    } else {
      this.openedBoardId = id;
    }
  }

  openedCardId: number | null = null;
  editingDescriptionCardId: number | null = null;
  descriptionErrorCardId = signal<number | null>(null);

  toggleCard(id: number) {
    if (this.openedCardId === id) {
      this.openedCardId = null;
    } else {
      this.openedCardId = id;
    }
  }
  creatingCardListId = signal<number | null>(null);
  creatingBoard = signal(false);
  creatingColumnBoardId = signal<number | null>(null);
  cardCreateErrorListId = signal<number | null>(null);
  boardCreateError = signal(false);
  columnCreateErrorBoardId = signal<number | null>(null);
  cardActionError = signal<{ cardId: number | null; message: string }>({
    cardId: null,
    message: '',
  });
  addCard(cardTitle: string, listId: number) {
    this.cardCreateErrorListId.set(null);
    const body = {
      title: cardTitle.trim(),
      taskListId: listId,
    };
    if (body.title === '') {
      return;
    }

    this.http.post('/api/cards', body).subscribe({
      next: () => {
        if (this.creatingCardListId() === listId) {
          this.creatingCardListId.set(null);
        }
        this.loadBoards();
      },
      error: () => this.cardCreateErrorListId.set(listId),
    });
  }

  addBoard(boardName: string) {
    this.boardCreateError.set(false);
    const body = { name: boardName.trim() };
    if (body.name == '') {
      return;
    }

    this.http.post('/api/boards', body).subscribe({
      next: () => {
        this.creatingBoard.set(false);
        this.loadBoards();
      },
      error: () => this.boardCreateError.set(true),
    });
  }

  addCol(colName: string, boardId: number) {
    this.columnCreateErrorBoardId.set(null);
    const body = { name: colName.trim(), boardId: boardId };
    if (body.name == '') {
      return;
    }

    this.http.post('/api/tasklists', body).subscribe({
      next: () => {
        if (this.creatingColumnBoardId() === boardId) {
          this.creatingColumnBoardId.set(null);
        }
        this.loadBoards();
      },
      error: () => this.columnCreateErrorBoardId.set(boardId),
    });
  }

  moveErrorCardId = signal<number | null>(null);

  moveCard(cardId: number, targetListId: string) {
    this.moveErrorCardId.set(null);
    const body = {
      taskListId: Number(targetListId),
    };
    this.http.patch(`/api/cards/${cardId}/move`, body).subscribe({
      next: () => this.loadBoards(),
      error: () => this.moveErrorCardId.set(cardId),
    });
  }

  changeCardTitle(cardId: number, cardName: string) {
    const title = cardName.trim();
    if (title === '') {
      this.cardActionError.set({ cardId, message: 'Введи название карточки.' });
      return;
    }
    this.cardActionError.set({ cardId: null, message: '' });
    this.http.patch(`/api/cards/${cardId}/title`, { title }).subscribe({
      next: () => this.loadBoards(),
      error: () => this.cardActionError.set({ cardId, message: 'Не удалось изменить название.' }),
    });
  }

  changeCardDesc(cardId: number, cardDesc: string) {
    this.descriptionErrorCardId.set(null);
    const body = { description: cardDesc };
    this.http.patch(`/api/cards/${cardId}/description`, body).subscribe({
      next: () => {
        if (this.editingDescriptionCardId === cardId) {
          this.editingDescriptionCardId = null;
        }
        this.loadBoards();
      },
      error: () => this.descriptionErrorCardId.set(cardId),
    });
  }

  changeDate(cardId: number, cardDate: string) {
    this.cardActionError.set({ cardId: null, message: '' });
    const body = { dueDate: cardDate || null };
    this.http.patch(`/api/cards/${cardId}/due-date`, body).subscribe({
      next: () => this.loadBoards(),
      error: () => this.cardActionError.set({ cardId, message: 'Не удалось изменить срок.' }),
    });
  }

  changePriority(cardId: number, priority: string) {
    this.cardActionError.set({ cardId: null, message: '' });
    this.http.patch(`/api/cards/${cardId}/priority`, { priority }).subscribe({
      next: () => this.loadBoards(),
      error: () => this.cardActionError.set({ cardId, message: 'Не удалось изменить приоритет.' }),
    });
  }

  deleteCard(cardId: number) {
    if (!window.confirm('Удалить карточку?')) {
      return;
    }
    this.cardActionError.set({ cardId: null, message: '' });
    this.http.delete(`/api/cards/${cardId}`).subscribe({
      next: () => this.loadBoards(),
      error: () => this.cardActionError.set({ cardId, message: 'Не удалось удалить карточку.' }),
    });
  }

  deleteBoard(boardId: number) {
    this.boardActionsId = null;
    if (!window.confirm('Удалить доску?')) {
      return;
    }

    this.boardDeleteErrorId.set(null);
    this.http.delete(`/api/boards/${boardId}`).subscribe({
      next: () => {
        if (this.openedBoardId === boardId) {
          this.openedBoardId = null;
        }
        this.loadBoards();
      },
      error: () => this.boardDeleteErrorId.set(boardId),
    });
  }

}
