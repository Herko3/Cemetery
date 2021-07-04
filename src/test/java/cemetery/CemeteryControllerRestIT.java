package cemetery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CemeteryControllerRestIT {

    @Autowired
    TestRestTemplate template;

    @BeforeEach
    void init() {
        template.delete("/api/cemetery");

        template.postForObject("/api/cemetery", new CreateParcelCommand("A-12-15", "John Doe",
                List.of(new Person("Sarah Connor", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10))),
                LocalDate.of(2022, 5, 12)), ParcelDTO.class);

        template.postForObject("/api/cemetery", new CreateParcelCommand("B-21-15", "Jack Doe",
                List.of(new Person("Jack Sparrow", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10))),
                LocalDate.of(2021, 5, 12)), ParcelDTO.class);


        template.postForObject("/api/cemetery", new CreateParcelCommand("C-18-16", "Jane Doe",
                List.of(new Person("Random Cooper", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10))),
                LocalDate.of(2021, 5, 12)), ParcelDTO.class);

    }

    @Test
    void testGetParcels() {
        List<ParcelDTO> result = template.exchange("/api/cemetery",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ParcelDTO>>() {
                })
                .getBody();

        assertThat(result)
                .hasSize(3)
                .extracting(ParcelDTO::getOwner)
                .containsExactly("John Doe", "Jack Doe", "Jane Doe");
    }

    @Test
    void testDeleteParcel() {
        template.delete("/api/cemetery/A-12-15");

        List<ParcelDTO> result = template.exchange("/api/cemetery",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ParcelDTO>>() {
                })
                .getBody();

        assertThat(result)
                .hasSize(2)
                .extracting(ParcelDTO::getParcelId)
                .containsExactly("B-21-15", "C-18-16");
    }

    @Test
    void testGetParcelByID() {
        ParcelDTO result = template.getForObject("/api/cemetery/B-21-15", ParcelDTO.class);

        assertEquals("Jack Sparrow", result.getCorpses().get(0).getName());
    }

    @Test
    void testUpdateWithList() {
        template.put("/api/cemetery/C-18-16", new UpdateParcelCommand(null,
                List.of(
                        new Person("Another One", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10)),
                        new Person("Bites Dust", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10))),
                null, null));

        ParcelDTO result = template.getForObject("/api/cemetery/C-18-16", ParcelDTO.class);

        List<Person> expected = List.of(
                new Person("Random Cooper", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10)),
                new Person("Another One", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10)),
                new Person("Bites Dust", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10)));

        assertEquals(expected, result.getCorpses());
        assertEquals("Jane Doe",result.getOwner());
    }

    @Test
    void testUpdateWithPerson(){
        template.put("/api/cemetery/C-18-16", new UpdateParcelCommand(null,null,null,
                new Person("Another One", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10))));

        ParcelDTO result = template.getForObject("/api/cemetery/C-18-16", ParcelDTO.class);

        List<Person> expected = List.of(
                new Person("Random Cooper", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10)),
                new Person("Another One", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10)));

        assertEquals(expected, result.getCorpses());
        assertEquals("Jane Doe",result.getOwner());

    }

    @Test
    void testUpdateWithOwner(){
        template.put("/api/cemetery/C-18-16", new UpdateParcelCommand("Jason Statham",null,null,
                new Person("Another One", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10))));

        ParcelDTO result = template.getForObject("/api/cemetery/C-18-16", ParcelDTO.class);

        assertEquals("Jason Statham", result.getOwner());
    }

    @Test
    void testUpdateWithExpiration(){
        template.put("/api/cemetery/C-18-16", new UpdateParcelCommand("Jason Statham",null,LocalDate.of(2025,12,15),
                new Person("Another One", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10))));

        ParcelDTO result = template.getForObject("/api/cemetery/C-18-16", ParcelDTO.class);

        assertEquals(LocalDate.of(2025,12,15),result.getExpirationDate());
    }

    @Test
    void testQueryStringOwner(){
        List<ParcelDTO> result = template.exchange("/api/cemetery?owner=jack doe",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ParcelDTO>>() {
                })
                .getBody();

        assertThat(result)
                .hasSize(1)
                .extracting(ParcelDTO::getParcelId)
                .containsOnly("B-21-15");
    }

    @Test
    void testQueryWithCorpse(){
        List<ParcelDTO> result = template.exchange("/api/cemetery?corpse=Random Cooper",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ParcelDTO>>() {
                })
                .getBody();

        assertThat(result)
                .hasSize(1)
                .extracting(ParcelDTO::getParcelId)
                .containsOnly("C-18-16");
    }

    @Test
    void testGetExpired(){
        List<ParcelDTO> result = template.exchange("/api/cemetery/expire",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ParcelDTO>>() {
                })
                .getBody();

        assertThat(result)
                .hasSize(2)
                .extracting(ParcelDTO::getParcelId)
                .contains("C-18-16");
    }

    @Test
    void testNotFound(){
        Problem problem = template.getForObject("/api/cemetery/C-32",Problem.class);

        assertEquals(Status.NOT_FOUND,problem.getStatus());
    }

    @Test
    void createWithInvalid(){
        Problem problem = template.postForObject("/api/cemetery", new CreateParcelCommand("", "",
                List.of(new Person("Jack Sparrow", LocalDate.of(1985, 5, 12), LocalDate.of(2015, 12, 10))),
                LocalDate.of(2021, 5, 12)), Problem.class);

        assertEquals(Status.BAD_REQUEST,problem.getStatus());
    }


}
