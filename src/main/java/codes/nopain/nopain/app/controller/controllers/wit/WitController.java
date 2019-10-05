package codes.nopain.nopain.app.controller.controllers.wit;

import codes.nopain.nopain.app.worker.wit.api.fetch.WitApi;
import codes.nopain.nopain.app.worker.wit.api.message.TextMessage;
import codes.nopain.nopain.app.worker.wit.process.WitProcessor;
import codes.nopain.nopain.app.worker.wit.resource.WitResource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WitController {
    private final WitApi witApi;

    @GetMapping("/api/wit/message")
    public List<TextMessage> message(@RequestParam(name = "q", required = true) String message, Principal principal) throws IOException {
        return witApi.user(principal.getName()).getKsMessages(message).toTextMessages();
    }
}
