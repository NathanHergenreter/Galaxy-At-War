import { inject } from '@angular/core/testing';
import mock from 'xhr-mock';
import { ChatService } from '../chat.service';

describe('ChatService', () => {
    // replace the real XHR object with the mock XHR object before each test

    const serverUrl = 'http://cs309-vc-1.misc.iastate.edu:8080/messages/';
    let service: ChatService;

    beforeEach(inject([ChatService], (userService) => {
        service = userService;
        mock.setup();
    }));

    // put the real XHR object back and clear the mocks after each test
    afterEach(() => mock.teardown());

    // Mocking the xhr call to get messages and testing that the call gets triggered successfully
    it('should get all messages with 200 status ', async () => {

        mock.get(serverUrl, {
            status: 200,
            reason: 'Created',
            body: '[{message:"Hi admin"}, {message:"hello brother"}]'
        });

        const user = await service.getMessages();

        expect(user).toEqual('[{message:"Hi admin"}, {message:"hello brother"}]');
    });

    // Mocking the xhr call to post/send messages and testing that the call gets triggered successfully
    it('should send a message ', async () => {

        const message = {
            message: 'Hi you'
        };

        mock.get(serverUrl, {
            status: 200,
            reason: 'Created',
            body: message
        });

        const user = await service.getMessages();

        expect(user).toEqual(message);
    });

});
