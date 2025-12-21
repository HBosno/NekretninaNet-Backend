package com.nekretninanet.backend.clrunner;

import com.nekretninanet.backend.dto.CreateSupportUserRequest;
import com.nekretninanet.backend.dto.UpdateUserDTO;
import com.nekretninanet.backend.model.*;
import com.nekretninanet.backend.repository.QueryRepository;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.ReviewRepository;
import com.nekretninanet.backend.repository.UserRepository;
import com.nekretninanet.backend.service.ReviewService;
import com.nekretninanet.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@Order(1)
public class UserCommandLineRunner implements CommandLineRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RealEstateRepository realEstateRepository;
    @Autowired
    private QueryRepository queryRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public void run(String... args) throws Exception {
        // TEST 1: da @PatchMapping("/admin/support-accounts/{id}") zaista mijenja pass i hashira...
        // uspjesan test ce prikazivat razlicite usernameove i hasheve
        /*
        CreateSupportUserRequest user1 = new CreateSupportUserRequest("Test Name",
                "Test lastName",
                "test",
                "testUhadfouasfd1232131213",
                "test",
                "mailtest",
                "123-456-789"
                );

        userService.createSupportUser(user1);
        Optional<User> nasUser = userRepository.findByUsername("test");
        System.out.println(nasUser.get().getUsername());
        System.out.println(nasUser.get().getHashPassword());
        UpdateUserDTO updateovani = new UpdateUserDTO("","" , "novo", "testUhadfouasfd12321312133232322323324423243", "","" ,"" );
        userService.updateSupportUser(1L, updateovani);
        Optional<User> nasUser1 = userRepository.findByUsername("novo");
        System.out.println(nasUser1.get().getUsername());
        System.out.println(nasUser1.get().getHashPassword());
        */
        // TEST 2: @DeleteMapping("/admin/support-accounts/{id}")
    //    testCascadingDelete();

        // TEST 3: @DeleteMapping("/support/regular-users-accounts/{id}") i @DeleteMapping("/user/account/{id}"), ista metoda
   //     testRegularUserCascadingDelete();

        // TEST 4: @DeleteMapping("/support/review/{id}")
  //      testDeleteReview();
/*
        userService.createRegularUser(user);

        User user = new User("Test Name",
                "Test lastName",
                "test",
                "test",
                "test",
                "mailtest",
                "123-456-789",
                UserType.USER
        );

        userService.createRegularUser(user);
        System.out.println("------------------------------------------------");
     //   System.out.println(userService.getAllSupportUsers());
        System.out.println("------------------------------------------------");
*/
    }

    private void testCascadingDelete() {
        System.out.println("\n------------------------------------------------");
        System.out.println("TEST 2: DELETE Support User Cascading");
        System.out.println("------------------------------------------------");

        // 1. KREIRANJE PODATAKA (SETUP)
        // Kreiramo Support Usera kojeg cemo brisati
        User supportUser = new User();
        supportUser.setUsername("supportBrisanje");
        supportUser.setFirstName("Brisko");
        supportUser.setLastName("Brisic");
        supportUser.setEmail("delete@me.com");
        supportUser.setHashPassword("sifra123");
        supportUser.setPhoneNumber("111-222");
        supportUser.setUserType(UserType.SUPPORT);
        userRepository.save(supportUser);
        Long supportId = supportUser.getId();

        // Kreiramo "Drugog" usera da vidimo da li ce njegovi podaci ostati (da nismo obrisali sve iz baze greškom)
        User otherUser = new User();
        otherUser.setUsername("prezivjeli");
        otherUser.setFirstName("Ostajem");
        otherUser.setLastName("Ziv");
        otherUser.setEmail("stay@me.com");
        otherUser.setHashPassword("sifra123");
        otherUser.setPhoneNumber("333-444");
        otherUser.setUserType(UserType.USER);
        userRepository.save(otherUser);

        // 2. KREIRANJE POVEZANIH ENTITETA

        // A) Nekretnina čiji je vlasnik Support User
        RealEstate estate = new RealEstate();
        estate.setTitle("Stan za brisanje");
        estate.setPrice(100000.0);
        estate.setUser(supportUser); // VEZA 1
        estate.setStatus(RealEstateStatus.ACTIVE);
        estate.setPublishDate(LocalDate.now());
        realEstateRepository.save(estate);

        // B) Query (Pitanje) gdje je Support User autor
        Query queryBySupport = new Query();
        queryBySupport.setQuestion("Moje pitanje?");
        queryBySupport.setUser(supportUser); // VEZA 2
        queryBySupport.setQueryType(QueryType.SUPPORT_REQUEST);
        queryBySupport.setStatus(QueryStatus.UNANSWERED);
        queryBySupport.setQueryDate(LocalDate.now());
        queryRepository.save(queryBySupport);

        // C) Query na nekretninu Support Usera (autor je netko drugi)
        Query queryOnEstate = new Query();
        queryOnEstate.setQuestion("Pitanje za tvoj stan?");
        queryOnEstate.setUser(otherUser);
        queryOnEstate.setRealEstate(estate); // VEZA 3 (preko nekretnine)
        queryOnEstate.setQueryType(QueryType.REAL_ESTATE_QUERY);
        queryOnEstate.setStatus(QueryStatus.UNANSWERED);
        queryOnEstate.setQueryDate(LocalDate.now());
        queryRepository.save(queryOnEstate);

        // D) Review (Recenzija) gdje je Support User autor
        Review reviewBySupport = new Review();
        reviewBySupport.setComment("Meni se ovo ne svidja");
        reviewBySupport.setRating(1);
        reviewBySupport.setUser(supportUser); // VEZA 4
        reviewBySupport.setDate(LocalDate.now());
        reviewBySupport.setStatus(ReviewStatus.ACTIVE);
        // Ovdje nam treba neka nekretnina za review, stavit cemo istu radi testa
        reviewBySupport.setRealEstate(estate);
        reviewRepository.save(reviewBySupport);

        // E) Review na nekretninu Support Usera (autor je netko drugi)
        Review reviewOnEstate = new Review();
        reviewOnEstate.setComment("Super stan!");
        reviewOnEstate.setRating(5);
        reviewOnEstate.setUser(otherUser);
        reviewOnEstate.setRealEstate(estate); // VEZA 5 (preko nekretnine)
        reviewOnEstate.setDate(LocalDate.now());
        reviewOnEstate.setStatus(ReviewStatus.ACTIVE);
        reviewRepository.save(reviewOnEstate);

        // 3. ISPIS STANJA PRIJE BRISANJA
        System.out.println(">> STANJE PRIJE BRISANJA:");
        System.out.println("Usera u bazi: " + userRepository.count());
        System.out.println("Nekretnina u bazi: " + realEstateRepository.count());
        System.out.println("Upita (Query) u bazi: " + queryRepository.count());
        System.out.println("Recenzija (Review) u bazi: " + reviewRepository.count());

        // Provjera da li podaci postoje
        boolean userExists = userRepository.existsById(supportId);
        System.out.println("Support user postoji? " + userExists);

        // 4. IZVRŠAVANJE BRISANJA
        System.out.println("\n>> BRISANJE ID: " + supportId + "...");
        try {
            userService.deleteSupportUserCascading(supportId);
            System.out.println("Metoda izvršena bez Exceptiona.");
        } catch (Exception e) {
            System.out.println("GRESKA PRILIKOM BRISANJA: " + e.getMessage());
            e.printStackTrace();
        }

        // 5. ISPIS STANJA POSLIJE BRISANJA (VERIFIKACIJA)
        System.out.println("\n>> STANJE POSLIJE BRISANJA:");

        Optional<User> deletedUser = userRepository.findById(supportId);
        System.out.println("Support user pronađen? (Treba biti false): " + deletedUser.isPresent());

        Optional<RealEstate> deletedEstate = realEstateRepository.findById(estate.getId());
        System.out.println("Nekretnina support usera pronađena? (Treba biti false): " + deletedEstate.isPresent());

        Optional<Query> deletedQuery1 = queryRepository.findById(queryBySupport.getId());
        System.out.println("Query gdje je on autor pronađen? (Treba biti false): " + deletedQuery1.isPresent());

        Optional<Query> deletedQuery2 = queryRepository.findById(queryOnEstate.getId());
        System.out.println("Query na njegovu nekretninu pronađen? (Treba biti false): " + deletedQuery2.isPresent());

        Optional<Review> deletedReview1 = reviewRepository.findById(reviewBySupport.getId());
        System.out.println("Review gdje je on autor pronađen? (Treba biti false): " + deletedReview1.isPresent());

        Optional<Review> deletedReview2 = reviewRepository.findById(reviewOnEstate.getId());
        System.out.println("Review na njegovu nekretninu pronađen? (Treba biti false): " + deletedReview2.isPresent());

        // Provjera da nismo obrisali previše (Other user mora ostati)
        Optional<User> survivor = userRepository.findById(otherUser.getId());
        System.out.println("Drugi user preživio? (Treba biti true): " + survivor.isPresent());
    }

    private void testRegularUserCascadingDelete() {
        System.out.println("\n------------------------------------------------");
        System.out.println("TEST 3: DELETE Regular User Cascading");
        System.out.println("------------------------------------------------");

        // 1. KREIRANJE PODATAKA (SETUP)

        // Kreiramo Regular Usera kojeg cemo brisati
        // BITNO: UserType.USER (inače će metoda baciti BadRequestException)
        User regularUser = new User();
        regularUser.setUsername("regularBrisanje");
        regularUser.setFirstName("Obicni");
        regularUser.setLastName("Korisnik");
        regularUser.setEmail("regular@delete.com");
        regularUser.setHashPassword("sifra123");
        regularUser.setPhoneNumber("555-666");
        regularUser.setUserType(UserType.USER);
        userRepository.save(regularUser);
        Long userId = regularUser.getId();

        // Kreiramo "Promatrača" (Support ili drugi user) da vidimo da ne brišemo sve živo
        User survivorUser = new User();
        survivorUser.setUsername("prezivjeliRegular");
        survivorUser.setFirstName("Ostajem");
        survivorUser.setLastName("Tu");
        survivorUser.setEmail("stay-reg@me.com");
        survivorUser.setHashPassword("sifra123");
        survivorUser.setPhoneNumber("777-888");
        survivorUser.setUserType(UserType.USER);
        userRepository.save(survivorUser);

        // 2. KREIRANJE POVEZANIH ENTITETA

        // A) Nekretnina čiji je vlasnik Regular User
        RealEstate estate = new RealEstate();
        estate.setTitle("Kuća regularnog korisnika");
        estate.setPrice(250000.0);
        estate.setUser(regularUser); // VEZA 1: Vlasnik
        estate.setStatus(RealEstateStatus.ACTIVE);
        estate.setPublishDate(LocalDate.now());
        realEstateRepository.save(estate);

        // B) Query (Pitanje) gdje je Regular User autor (npr. pitao za neku drugu nekretninu)
        Query queryByUser = new Query();
        queryByUser.setQuestion("Kolika je režija?");
        queryByUser.setUser(regularUser); // VEZA 2: Autor pitanja
        queryByUser.setQueryType(QueryType.REAL_ESTATE_QUERY);
        queryByUser.setStatus(QueryStatus.UNANSWERED);
        queryByUser.setQueryDate(LocalDate.now());
        // Radi jednostavnosti, vežemo na njegovu nekretninu, ili bilo koju drugu
        queryByUser.setRealEstate(estate);
        queryRepository.save(queryByUser);

        // C) Query na nekretninu Regular Usera (autor je netko drugi - survivor)
        // OVO JE BITNO: Treba se obrisati jer nestaje nekretnina!
        Query queryOnEstate = new Query();
        queryOnEstate.setQuestion("Je li cijena fiksna?");
        queryOnEstate.setUser(survivorUser);
        queryOnEstate.setRealEstate(estate); // VEZA 3: Pitanje vezano za nekretninu koja se briše
        queryOnEstate.setQueryType(QueryType.REAL_ESTATE_QUERY);
        queryOnEstate.setStatus(QueryStatus.UNANSWERED);
        queryOnEstate.setQueryDate(LocalDate.now());
        queryRepository.save(queryOnEstate);

        // D) Review (Recenzija) gdje je Regular User autor
        Review reviewByUser = new Review();
        reviewByUser.setComment("Nije loše, ali skupo.");
        reviewByUser.setRating(3);
        reviewByUser.setUser(regularUser); // VEZA 4: Autor recenzije
        reviewByUser.setDate(LocalDate.now());
        reviewByUser.setStatus(ReviewStatus.ACTIVE);
        reviewByUser.setRealEstate(estate);
        reviewRepository.save(reviewByUser);

        // E) Review na nekretninu Regular Usera (autor je survivor)
        // I ovo se mora obrisati jer brišemo nekretninu
        Review reviewOnEstate = new Review();
        reviewOnEstate.setComment("Vlasnik je super!");
        reviewOnEstate.setRating(5);
        reviewOnEstate.setUser(survivorUser);
        reviewOnEstate.setRealEstate(estate); // VEZA 5: Recenzija na nekretninu koja se briše
        reviewOnEstate.setDate(LocalDate.now());
        reviewOnEstate.setStatus(ReviewStatus.ACTIVE);
        reviewRepository.save(reviewOnEstate);

        // 3. ISPIS STANJA PRIJE BRISANJA
        System.out.println(">> STANJE PRIJE BRISANJA:");
        System.out.println("User ID: " + userId + " postoji: " + userRepository.existsById(userId));
        System.out.println("Nekretnina ID: " + estate.getId() + " postoji: " + realEstateRepository.existsById(estate.getId()));

        // 4. IZVRŠAVANJE BRISANJA
        System.out.println("\n>> BRISANJE Regular Usera ID: " + userId + "...");
        try {
            // Ovdje pozivamo metodu za REGULAR user-a
            userService.deleteRegularUserCascading(userId);
            System.out.println("Metoda izvršena bez Exceptiona.");
        } catch (Exception e) {
            System.out.println("GRESKA PRILIKOM BRISANJA: " + e.getMessage());
            e.printStackTrace();
        }

        // 5. ISPIS STANJA POSLIJE BRISANJA (VERIFIKACIJA)
        System.out.println("\n>> STANJE POSLIJE BRISANJA:");

        boolean userDeleted = !userRepository.existsById(userId);
        System.out.println("Regular user obrisan? " + userDeleted);

        boolean estateDeleted = !realEstateRepository.existsById(estate.getId());
        System.out.println("Nekretnina usera obrisana? " + estateDeleted);

        boolean queryByUserDeleted = !queryRepository.existsById(queryByUser.getId());
        System.out.println("Query gdje je on autor obrisan? " + queryByUserDeleted);

        boolean queryOnEstateDeleted = !queryRepository.existsById(queryOnEstate.getId());
        System.out.println("Query na njegovu nekretninu obrisan? " + queryOnEstateDeleted);

        boolean reviewByUserDeleted = !reviewRepository.existsById(reviewByUser.getId());
        System.out.println("Review gdje je on autor obrisan? " + reviewByUserDeleted);

        boolean reviewOnEstateDeleted = !reviewRepository.existsById(reviewOnEstate.getId());
        System.out.println("Review na njegovu nekretninu obrisan? " + reviewOnEstateDeleted);

        // Provjera preživjelog
        boolean survivorExists = userRepository.existsById(survivorUser.getId());
        System.out.println("Drugi user preživio? " + survivorExists);

        if (userDeleted && estateDeleted && queryByUserDeleted && queryOnEstateDeleted && survivorExists) {
            System.out.println("\n*** TEST USPJEŠAN! ***");
        } else {
            System.out.println("\n*** TEST NEUSPJEŠAN! Provjeri logove. ***");
        }
    }

    private void testDeleteReview() {
        System.out.println("\n------------------------------------------------");
        System.out.println("TEST 4: DELETE Review (Hard Delete)");
        System.out.println("------------------------------------------------");

        // 1. SETUP: Trebaju nam User i Nekretnina da bi Review mogao postojati
        User author = new User();
        author.setUsername("reviewPisac");
        author.setFirstName("Marko");
        author.setLastName("Markovic");
        author.setEmail("pisac@test.com");
        author.setHashPassword("pass123");
        author.setPhoneNumber("999-000");
        author.setUserType(UserType.USER);
        userRepository.save(author);

        RealEstate estate = new RealEstate();
        estate.setTitle("Stan za recenziju");
        estate.setPrice(120000.0);
        // Postavimo autora nekretnine (moze biti isti ili drugi user, nebitno za ovaj test)
        estate.setUser(author);
        estate.setStatus(RealEstateStatus.ACTIVE);
        estate.setPublishDate(LocalDate.now());
        realEstateRepository.save(estate);

        // 2. KREIRANJE RECENZIJE KOJU BRIŠEMO
        Review review = new Review();
        review.setRating(5);
        review.setComment("Ovo treba biti obrisano!");
        review.setUser(author);
        review.setRealEstate(estate);
        review.setStatus(ReviewStatus.ACTIVE);
        review.setDate(LocalDate.now());
        reviewRepository.save(review);

        Long reviewId = review.getId();

        // 3. PROVJERA PRIJE BRISANJA
        System.out.println(">> PRIJE BRISANJA:");
        System.out.println("Review ID " + reviewId + " postoji u bazi? " + reviewRepository.existsById(reviewId));

        // 4. IZVRŠAVANJE BRISANJA
        System.out.println("\n>> BRISANJE Review ID: " + reviewId + "...");
        try {
            reviewService.deleteReview(reviewId);
            System.out.println("Metoda reviewService.deleteReview izvršena.");
        } catch (Exception e) {
            System.out.println("GREŠKA: " + e.getMessage());
        }

        // 5. PROVJERA POSLIJE BRISANJA
        System.out.println("\n>> POSLIJE BRISANJA:");

        boolean reviewExists = reviewRepository.existsById(reviewId);
        System.out.println("Review postoji? (Mora biti false): " + reviewExists);

        // Sigurnosna provjera: Brisanje recenzije NE SMIJE obrisati Usera ili Nekretninu
        boolean userStillExists = userRepository.existsById(author.getId());
        System.out.println("User još postoji? (Mora biti true): " + userStillExists);

        boolean estateStillExists = realEstateRepository.existsById(estate.getId());
        System.out.println("Nekretnina još postoji? (Mora biti true): " + estateStillExists);

        if (!reviewExists && userStillExists && estateStillExists) {
            System.out.println("\n*** TEST USPJEŠAN! ***");
        } else {
            System.out.println("\n*** TEST NEUSPJEŠAN! ***");
        }
    }

}


