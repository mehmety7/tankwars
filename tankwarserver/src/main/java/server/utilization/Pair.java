package server.utilization;
import lombok.*;
/* Bir tank birden fazla mermiye sahip olabileceğinden,
*  Birden çok ilişkisini sağlamak adına c++'daki pair classına benzer bir class yazdım. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pair<T, U> {
    private T first;
    private U second;
}