package codes.nopain.nopain.app.controller.controllers.test;

import codes.nopain.nopain.app.worker.wit.api.fetch.WitApi;
import codes.nopain.nopain.app.worker.wit.api.message.KsMessage;
import codes.nopain.nopain.app.worker.wit.api.message.TextMessage;
import codes.nopain.nopain.app.worker.wit.resource.WitResource;
import codes.nopain.nopain.app.worker.wit.resource.response.WitResponse;
import codes.nopain.nopain.app.worker.wit.process.WitProcessor;
import codes.nopain.nopain.app.worker.wit.process.command.WitCommands;
import codes.nopain.nopain.app.worker.wit.process.message.message.WitMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WitTestController {
    private final WitResource witResource;
    private final WitProcessor witProcessor;
    private final WitApi witApi;

    @GetMapping("/api/wit/test")
    public WitMessage wit(@RequestParam(name = "q", required = true) String message, Principal principal) throws IOException {
        WitResponse response = witResource.fetch(message);
        WitMessage witMessage = witProcessor
                .user(principal.getName())
                .transform(response)
                .regexFilter()
                .postFilter()
                .getWitMessage();
        /*WitCommand witCommand = witMessageProcessor
                .transform(response)
                .regexFilter()
                .postFilter()
                .toCommand();*/

        return witMessage;
    }

    @GetMapping("/api/wit/command")
    public WitCommands command(@RequestParam(name = "q", required = true) String message, Principal principal) throws IOException {
        WitResponse response = witResource.fetch(message);
        WitCommands command = witProcessor
                .user(principal.getName())
                .transform(response)
                .regexFilter()
                .postFilter()
                .commandFilter()
                .getWitCommands();
        /*WitCommand witCommand = witMessageProcessor
                .transform(response)
                .regexFilter()
                .postFilter()
                .toCommand();*/

        return command;
    }
}
