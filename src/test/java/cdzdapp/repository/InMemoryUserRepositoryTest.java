package cdzdapp.repository;

import cdzdapp.domain.User;
import cdzdapp.repository.InMemoryUserRepository;
import cdzdapp.repository.UserRepository;
import cdzdapp.test.categories.SmallTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(SmallTest.class)
public class InMemoryUserRepositoryTest {

    private static final String MR_TEST = "Mr. Test";

    @Test
    public void addUser() {
        User mrTest = new User(MR_TEST);

        UserRepository repository = InMemoryUserRepository.INSTANCE;
        assertNull(repository.getUserByName(MR_TEST));
        repository.addUser(mrTest);
        assertEquals(mrTest, repository.getUserByName(MR_TEST));
    }
}
