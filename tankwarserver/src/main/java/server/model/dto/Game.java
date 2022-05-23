package server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    private Integer id;
    private Map<Integer, Integer> players; // playerId - point
    private Integer tourNumber;
    private Double shootingSpeed;
    private String mapType;
    private Boolean isStarted;

}
