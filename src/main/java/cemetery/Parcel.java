package cemetery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parcel {

    private long id;
    private String parcelId;
    private String owner;
    private List<Person> corpses;
    private LocalDate expirationDate;

    public void updateParcel(UpdateParcelCommand command){
        if(command.getOwner() != null){
            setOwner(command.getOwner());
        }

        if(command.getCorpses() != null){
            addCorpses(command.getCorpses());
        }

        if(command.getCorpse() != null){
            addCorpse(command.getCorpse());
        }

        if(command.getExpirationDate() != null){
            setExpirationDate(command.getExpirationDate());
        }
    }

    public void addCorpse(Person person){
        corpses.add(person);
    }

    public void addCorpses(List<Person> persons){
        corpses.addAll(persons);
    }

    public boolean isCorpseWithNameInThisParcel(String name) {
        Optional<String> result = corpses.stream()
                .map(Person::getName)
                .filter(n -> n.equalsIgnoreCase(name))
                .findAny();

        if (result.isPresent()) {
            return true;
        }
        return false;
    }

}
