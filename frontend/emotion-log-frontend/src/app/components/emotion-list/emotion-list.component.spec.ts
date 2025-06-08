import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmotionListComponent } from './emotion-list.component';

describe('EmotionListComponent', () => {
  let component: EmotionListComponent;
  let fixture: ComponentFixture<EmotionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmotionListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmotionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
