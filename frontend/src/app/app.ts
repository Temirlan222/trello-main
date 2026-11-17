import { Component, signal,inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
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
interface TaskList{
  id: number;
  name: string;
  cards: Card[];
  isDone: boolean;
  }

interface Board{
  id:number;
  name: string;
  lists: TaskList[];
  }

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('frontend');
  private readonly http = inject(HttpClient);
  loadError = signal(false);
  isLoading = signal(true);

  focusCards = signal <FocusCard[]>([]);
  focusedBoardId = signal<number | null>(null);
  focusErrorBoardId = signal<number | null>(null);

  loadFocus(boardId: number) {
    this.focusErrorBoardId.set(null);

  this.http.get<FocusCard[]>(`/api/boards/${boardId}/focus`).subscribe({
    next: (cards) =>{
        this.focusCards.set(cards);
        this.focusedBoardId.set(boardId);
        },
       error: () => {
            this.focusCards.set([]);
            this.focusedBoardId.set(null);
            this.focusErrorBoardId.set(boardId);
          }
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
      }
    });
  }

  openedBoardId: number | null = null;

  toggleBoard(id:number){
    if(this.openedBoardId === id){
      this.openedBoardId = null;
      }
    else{
      this.openedBoardId = id;
      }
    }

  openedCardId: number | null = null;

  toggleCard(id:number){
    if(this.openedCardId === id){
      this.openedCardId = null
      }
    else{
      this.openedCardId = id;
      }
    }

  addCard(cardTitle:string, listId:number){
    const body = {
      title: cardTitle.trim(),
      taskListId: listId
    };
    this.http.post('/api/cards',body).subscribe({
      next: () => this.loadBoards()
    });
    }

  addBoard(boardName:string){
    const body = {
      name:boardName.trim()
      }
      if(body.name == ""){
        return;
        }
      this.http.post('/api/boards',body).subscribe({
        next: () => this.loadBoards()
        });
    }



  moveCard(cardId:number, targetListId:string){
    const body = {
        taskListId: Number(targetListId)
      };
    this.http.patch(`/api/cards/${cardId}/move`,body).subscribe({
      next: () => this.loadBoards()
    });
    }



}
