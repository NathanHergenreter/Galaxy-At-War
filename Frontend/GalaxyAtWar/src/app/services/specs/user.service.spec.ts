import { inject } from '@angular/core/testing';
import mock from 'xhr-mock';
import { UserService } from '../user.service';

describe('UserService', () => {
    // replace the real XHR object with the mock XHR object before each test

    const serverUrl = 'http://cs309-vc-1.misc.iastate.edu:8080/user/users';
    let service: UserService;
    
    beforeEach(inject([UserService], (userService) => {
        service = userService;
        mock.setup();
    }));

    // put the real XHR object back and clear the mocks after each test
    afterEach(() => mock.teardown());

    it('should get users with 200 status ', async () => {

        mock.get(serverUrl, {
            status: 200,
            reason: 'Created',
            body: '[{id:"abc-123"}, {id:"abc-456"}]'
        });

        const user = await service.getUsers();

        expect(user).toEqual('[{id:"abc-123"}, {id:"abc-456"}]');
    });

});
