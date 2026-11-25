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

  loadFocus(boardId: number) {
    this.focusErrorBoardId.set(null);

    this.http.get<FocusCard[]>(`/api/boards/${boardId}/focus`).subscribe({
      next: (cards) => {
        this.focusCards.set(cards);
        this.focusedBoardId.set(boardId);
      },
      error: () => {
        this.focusCards.set([]);
        this.focusedBoardId.set(null);
        this.focusErrorBoardId.set(boardId);
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

  toggleBoard(id: number) {
    if (this.openedBoardId === id) {
      this.openedBoardId = null;
    } else {
      this.openedBoardId = id;
    }
  }

  openedCardId: number | null = null;

  toggleCard(id: number) {
    if (this.openedCardId === id) {
      this.openedCardId = null;
    } else {
      this.openedCardId = id;
    }
  }
  cardCreateErrorListId = signal<number | null>(null);
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
      next: () => this.loadBoards(),
      error: () => this.cardCreateErrorListId.set(listId),
    });
  }

  addBoard(boardName: string) {
    const body = { name: boardName.trim() };
    if (body.name == '') {
      return;
    }

    this.http.post('/api/boards', body).subscribe({
      next: () => this.loadBoards(),
    });
  }

  addCol(colName: string, boardId: number) {
    const body = { name: colName.trim(), boardId: boardId };
    if (body.name == '') {
      return;
    }

    this.http.post('/api/tasklists', body).subscribe({
      next: () => this.loadBoards(),
    });
  }

  moveCard(cardId: number, targetListId: string) {
    const body = {
      taskListId: Number(targetListId),
    };
    this.http.patch(`/api/cards/${cardId}/move`, body).subscribe({
      next: () => this.loadBoards(),
    });
  }

  changeCardTitle(cardId: number,cardName:string){
    const body = {
      title: cardName,
    };
    this.http.patch(`/api/cards/${cardId}/title`,body).subscribe({
        next: () => this.loadBoards(),
      })
    }

  changeCardDesc(cardId:number, cardDesc:string){
    const body = {
        description:cardDesc,
      };
    this.http.patch(`/api/cards/${cardId}/description`,body).subscribe({
        next: () => this.loadBoards()
      })
    }

  changeDate(cardId: number, cardDate: string) {
    const body = { dueDate: cardDate || null };
    this.http.patch(`/api/cards/${cardId}/due-date`, body).subscribe({
      next: () => this.loadBoards(),
    });
  }

  changePriority(cardId: number, priority: string) {
    this.http.patch(`/api/cards/${cardId}/priority`, { priority }).subscribe({
      next: () => this.loadBoards(),
    });
  }

  deleteCard(cardId:number){
    if (!window.confirm('Удалить карточку?')){
      return;
      }


    this.http.delete(`/api/cards/${cardId}`).subscribe({
      next: () => this.loadBoards()});

    }

  deleteBoard(boardId: number) {
    this.boardActionsId = null;
    if (!window.confirm('Удалить доску?')) {
      return;
    }

    this.http.delete(`/api/boards/${boardId}`).subscribe({
      next: () => this.loadBoards(),
    });



  }

}
