import { TestBed } from '@angular/core/testing';

import { BookCreateGuard } from './book-create.guard';

describe('BookCreateGuard', () => {
  let guard: BookCreateGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(BookCreateGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
