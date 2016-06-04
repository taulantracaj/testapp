package cdzdapp.repository;

import cdzdapp.domain.Friend;
import cdzdapp.domain.User;
import cdzdapp.repository.FriendRepository;
import cdzdapp.repository.InMemoryFriendRepository;
import cdzdapp.repository.InMemoryUserRepository;
import cdzdapp.repository.UserRepository;
import cdzdapp.test.categories.SmallTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(SmallTest.class)
public class InMemoryFriendRepositoryTest {
    @Test
    public void addFriend() {
        User marge = new User("Marge");
        User maggie = new User("Maggie");

        UserRepository userRepository = InMemoryUserRepository.INSTANCE;
        userRepository.addUser(marge);
        userRepository.addUser(maggie);

        Friend barney = new Friend(marge, "Barney", "Gumble");

        FriendRepository friendRepository = InMemoryFriendRepository.INSTANCE;
        friendRepository.addFriend(barney);

        assertTrue(friendRepository.getFriendsForUser(marge).contains(barney));
        assertFalse(friendRepository.getFriendsForUser(maggie).contains(barney));
    }
}
