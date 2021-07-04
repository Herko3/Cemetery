package cemetery;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/cemetery")
public class CemeteryController {

    private CemeteryService service;

    public CemeteryController(CemeteryService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParcelDTO> getParcels(@RequestParam Optional<String> owner, @RequestParam Optional<String> corpse){
        return service.getParcels(owner,corpse);
    }

    @GetMapping("{parcelId}")
    public ParcelDTO getParcelById(@PathVariable ("parcelId") String parcelId){
        return service.getParcelById(parcelId);
    }

    @GetMapping("/expire")
    public List<ParcelDTO> getParcelsWithExpirationThisYear(){
        return service.getParcelsWithExpirationThisYear();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParcelDTO addParcel(@Valid @RequestBody CreateParcelCommand command){
        return service.addParcel(command);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(){
        service.deleteAll();
    }

    @DeleteMapping("{parcelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByParcelId(@PathVariable ("parcelId") String parcelId){
        service.deleteByParcelId(parcelId);
    }

    @PutMapping("{parcelId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ParcelDTO updateParcel(@PathVariable ("parcelId") String parcelId, @RequestBody UpdateParcelCommand command){
        return service.updateParcel(parcelId,command);
    }

    @ExceptionHandler(NoParcelFoundException.class)
    public ResponseEntity<Problem> handleNotFound(NoParcelFoundException e){
        Problem problem = Problem.builder()
                .withType(URI.create("parcels/not-found"))
                .withTitle("Not Found")
                .withStatus(Status.NOT_FOUND)
                .withDetail(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
