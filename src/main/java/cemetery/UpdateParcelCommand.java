package cemetery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateParcelCommand {

    private String owner;
    private List<Person> corpses;
    private LocalDate expirationDate;
    private Person corpse;

}
