package codes.nopain.nopain.app.controller.controllers.user.settings;

import codes.nopain.nopain.app.controller.exception.NoContentException;
import codes.nopain.nopain.app.database.document.UserSetting;
import codes.nopain.nopain.app.database.pojo.setting.KmaAccount;
import codes.nopain.nopain.app.database.repository.UserSettingsRepository;
import codes.nopain.nopain.app.worker.schedule.utils.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserSettingController {
    private final UserSettingsRepository userSettingsRepository;
    private final ScheduleUtils scheduleUtils;

    @GetMapping("/api/user/settings")
    public UserSetting get(Principal principal) {
        return userSettingsRepository.findByEmail(principal.getName())
                .orElseThrow(NoContentException::new);
    }

    @PostMapping("/api/user/settings")
    public UserSetting post(Principal principal, @RequestBody KmaAccount account) {
        UserSetting userSetting = userSettingsRepository.findByEmail(principal.getName())
                .orElseGet(() -> userSettingsRepository.save(
                        UserSetting.builder()
                                .email(principal.getName())
                                .kmaAccount(account)
                                .build()
                ));
        userSetting.setKmaAccount(account);
        userSettingsRepository.save(userSetting);
        scheduleUtils.syncSchedule(principal);

        return userSetting;
    }
}
