package codes.nopain.nopain.app.worker.global.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TimeDuration {
    private LocalTime start;
    private LocalTime end;
}
