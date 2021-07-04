package cemetery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private String name;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;

}
