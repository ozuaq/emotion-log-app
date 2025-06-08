import { TestBed } from '@angular/core/testing';

import { EmotionLogService } from './emotion-log.service';

describe('ApiService', () => {
  let service: EmotionLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmotionLogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
