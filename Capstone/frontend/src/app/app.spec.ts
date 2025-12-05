import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app';

describe('AppComponent', () => {
  it('should create the app', async () => {
    const fixture = await TestBed.configureTestingModule({
      imports: [AppComponent]
    }).createComponent(AppComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
