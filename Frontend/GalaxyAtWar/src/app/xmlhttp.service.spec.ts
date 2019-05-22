import { TestBed, fakeAsync, tick } from '@angular/core/testing';

import { XmlhttpService } from './services/xmlhttp.service';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';

describe('XmlhttpService', () => {
  let service: XmlhttpService;
  let mockHttp: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [XmlhttpService]
    })

    service = TestBed.get(XmlhttpService);
    mockHttp = TestBed.get(HttpTestingController);
  });

  it('should be created', () => {
    const service: XmlhttpService = TestBed.get(XmlhttpService);
    expect(service).toBeTruthy();
  });
});
