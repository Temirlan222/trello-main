import { Component, signal,inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
interface Card {
  id: number;
  title: string;
  priority: string;
  dueDate: string;
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




  boards = signal<Board[]>([]);
   ngOnInit(){
    this.http.get<Board[]>('/api/boards').subscribe(serverBoards => {
      this.boards.set(serverBoards);
      })
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

}
