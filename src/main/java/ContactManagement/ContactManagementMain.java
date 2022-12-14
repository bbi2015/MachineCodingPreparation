package ContactManagement;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class ContactManagementActivity {
    void updateContact(@NonNull final String firstName, @NonNull final String lastName,
                       @NonNull final String contacNumber) {}
    List<ContactDetails> searchByPrefix(@NonNull final String prefix) {
        return Collections.emptyList();
    }

    List<ContactDetails> searchByFirstName(@NonNull final String firstName) {
        return Collections.emptyList();
    }

    List<ContactDetails> searchByLastName(@NonNull final String lastName) {
        return Collections.emptyList();
    }
}

class UpdateContactDetails extends ContactManagementActivity {

    private final ContactManagementDao contactManagementDao = ContactManagementInMemoryDaoImpl.getInstance();

    @Override
    void updateContact(@NonNull String firstName, @NonNull String lastName, @NonNull String contacNumber) {
        contactManagementDao.updateContact(firstName, lastName, contacNumber);
    }
}

class SearchContactDetails extends ContactManagementActivity {

    private final ContactManagementDao contactManagementDao = ContactManagementInMemoryDaoImpl.getInstance();

    @Override
    List<ContactDetails> searchByPrefix(@NonNull String prefix) {
        return contactManagementDao.searchByPrefix(prefix);
    }

    @Override
    List<ContactDetails> searchByFirstName(@NonNull String firstName) {
        return contactManagementDao.searchByFirstName(firstName);
    }

    @Override
    List<ContactDetails> searchByLastName(@NonNull String lastName) {
        return contactManagementDao.searchByLastName(lastName);
    }
}

@Builder
@Getter
class ContactDetails {
    private final String firstName;
    private final String lastName;
    private final String contactNumber;
}

interface ContactManagementDao {
    void updateContact(String firstName, String lastName, String contactNumber);
    List<ContactDetails> searchByPrefix(String prefix);
    List<ContactDetails> searchByLastName(String lastName);
    List<ContactDetails> searchByFirstName(String firstName);
}

@Builder
@Getter
class ContactDetailsTrie {
    private final Map<Character, ContactDetailsTrie> nextNode;
    private String firstName;
    private String lastName;
    private String contactNumber;

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setContactNumber(final String contactNumber) {
        this.contactNumber = contactNumber;
    }
}

class ContactManagementInMemoryDaoImpl implements ContactManagementDao {

    private static final ContactManagementDao CONTACT_MANAGEMENT_DAO = new ContactManagementInMemoryDaoImpl();
    private final ContactDetailsTrie rootNode = ContactDetailsTrie.builder().nextNode(new HashMap<>()).build();

    public static ContactManagementDao getInstance() {
        return CONTACT_MANAGEMENT_DAO;
    }

    @Override
    public void updateContact(String firstName, String lastName, String contactNumber) {
        final ContactDetailsTrie destinationNode = this.getNodeWithFirstAndLastName(firstName, lastName);

        destinationNode.setLastName(lastName);
        destinationNode.setFirstName(firstName);
        destinationNode.setContactNumber(contactNumber);

        final ContactDetailsTrie destinationNodeWithLastName = this.getNodeWithFirstAndLastName(lastName, firstName);

        destinationNodeWithLastName.setLastName(lastName);
        destinationNodeWithLastName.setFirstName(firstName);
        destinationNodeWithLastName.setContactNumber(contactNumber);
    }

    private ContactDetailsTrie getNodeWithFirstAndLastName(final String firstName, final String lastName) {
        final ContactDetailsTrie rootWithFirstName = getTrieNode(rootNode, firstName, true);
        return getTrieNode(rootWithFirstName, lastName, true);
    }

    @Override
    public List<ContactDetails> searchByPrefix(String prefix) {
        final List<ContactDetails> contactDetails = new ArrayList<>();
        final ContactDetailsTrie trieNodeWithPrefix = this.getTrieNode(rootNode, prefix, false);
        this.fetchContactDetails(trieNodeWithPrefix, contactDetails);
        return contactDetails;
    }

    @Override
    public List<ContactDetails> searchByLastName(String lastName) {
        final List<ContactDetails> contactDetails = searchByPrefix(lastName);
        return contactDetails.stream()
                .filter(contactDetails1 -> contactDetails1.getLastName().equals(lastName))
                .collect(Collectors.toList());
    }

    @Override
    public List<ContactDetails> searchByFirstName(String firstName) {
        final List<ContactDetails> contactDetails = searchByPrefix(firstName);
        return contactDetails.stream()
                .filter(contactDetails1 -> contactDetails1.getFirstName().equals(firstName))
                .collect(Collectors.toList());
    }

    private ContactDetailsTrie getTrieNode(final ContactDetailsTrie root, final String name,
                                           final boolean isUpdateRequired) {
        if (StringUtils.isBlank(name)) {
            return root;
        }
        if (!isUpdateRequired && !root.getNextNode().containsKey(name.charAt(0))) {
            return null;
        }
        final ContactDetailsTrie nextNode = root.getNextNode().getOrDefault(name.charAt(0),
                ContactDetailsTrie.builder().nextNode(new HashMap<>()).build());
        if (isUpdateRequired) {
            root.getNextNode().putIfAbsent(name.charAt(0), nextNode);
        }
        return getTrieNode(nextNode, name.substring(1), isUpdateRequired);
    }

    private void fetchContactDetails(final ContactDetailsTrie node, final List<ContactDetails> contactDetails) {
        if (node == null) {
            return;
        }
        if (node.getContactNumber() != null) {
            contactDetails.add(ContactDetails.builder()
                    .firstName(node.getFirstName())
                    .lastName(node.getLastName())
                    .contactNumber(node.getContactNumber()).build());
        }
        node.getNextNode().values().forEach(contactDetailsTrie -> fetchContactDetails(contactDetailsTrie, contactDetails));
    }
}

public class ContactManagementMain {
    public static void main(String[] args) {
        final ContactManagementActivity updateActivity = new UpdateContactDetails();
        final ContactManagementActivity searchActivity = new SearchContactDetails();

        updateActivity.updateContact("arijit", "debnath", "8295146953");
        updateActivity.updateContact("arijit", "deb", "9612650243");

        List<ContactDetails> contactDetails = searchActivity.searchByFirstName("arijit");
        print(contactDetails);

        print(searchActivity.searchByLastName("deb"));
        print(searchActivity.searchByPrefix("hdhd"));
    }

    public static void print(final List<ContactDetails> contactDetails) {
        contactDetails.forEach(contactDetails1 -> {
            System.out.println(contactDetails1.getFirstName() + " " + contactDetails1.getLastName() + " " + contactDetails1.getContactNumber());
        });
    }
}
