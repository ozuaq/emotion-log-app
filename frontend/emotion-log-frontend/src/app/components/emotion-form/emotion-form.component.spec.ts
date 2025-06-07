import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmotionFormComponent } from './emotion-form.component';

describe('EmotionFormComponent', () => {
  let component: EmotionFormComponent;
  let fixture: ComponentFixture<EmotionFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmotionFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmotionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
