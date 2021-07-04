package cemetery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateParcelCommand {

    @NotBlank
    private String parcelId;

    private String owner;
    private List<Person> corpses;
    private LocalDate expirationDate;
}
