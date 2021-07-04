package cemetery;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CemeteryService {

    private List<Parcel> parcels = new ArrayList<>();

    private ModelMapper mapper;
    private AtomicLong idGenerator = new AtomicLong();

    public CemeteryService(ModelMapper mapper) {
        this.mapper = mapper;
    }


    public List<ParcelDTO> getParcels(Optional<String> owner, Optional<String> corpse) {
        return parcels.stream()
                .filter(p -> owner.isEmpty() || p.getOwner().equalsIgnoreCase(owner.get()))
                .filter(p -> corpse.isEmpty() || p.isCorpseWithNameInThisParcel(corpse.get()))
                .map(p -> mapper.map(p, ParcelDTO.class))
                .toList();
    }

    public ParcelDTO addParcel(CreateParcelCommand command) {
        LocalDate exp = command.getExpirationDate();
        if(exp == null){
            exp = LocalDate.now().plusYears(26);
        }
        Parcel parcel = new Parcel(idGenerator.incrementAndGet(), command.getParcelId(), command.getOwner(), command.getCorpses(), exp);
        parcels.add(parcel);

        return mapper.map(parcel, ParcelDTO.class);
    }

    public ParcelDTO getParcelById(String parcelId) {
        return mapper.map(findByParcelId(parcelId),ParcelDTO.class);
    }

    private Parcel findByParcelId(String id) {
        return parcels.stream()
                .filter(p -> p.getParcelId().equalsIgnoreCase(id))
                .findAny()
                .orElseThrow(() -> new NoParcelFoundException("No parcel with ParcelId: " + id));
    }

    public List<ParcelDTO> getParcelsWithExpirationThisYear() {
        return parcels.stream()
                .filter(p->p.getExpirationDate().getYear() == LocalDate.now().getYear())
                .map(p->mapper.map(p,ParcelDTO.class))
                .toList();
    }

    public void deleteAll() {
        parcels.clear();
        idGenerator = new AtomicLong();
    }

    public ParcelDTO updateParcel(String parcelId, UpdateParcelCommand command) {
        Parcel parcel = findByParcelId(parcelId);
        parcel.updateParcel(command);
        return mapper.map(parcel,ParcelDTO.class);
    }

    public void deleteByParcelId(String parcelId) {
        parcels.remove(findByParcelId(parcelId));
    }
}
