package quickfix.examples.utility;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import quickfix.InvalidMessage;
import quickfix.Message;

import static com.google.common.base.Preconditions.checkArgument;

public class CompareMessages {
    private static String SEPARATOR = "\001";
    private static int[] excludeTags = {9, 10, 11, 17, 19, 37, 41, 52, 60};

    public static void compareMessages(List<Message> actualMessages,
                                       char delimiter, List<String> expectedMessageStrs)
            throws InvalidMessage {
        compareMessages(actualMessages, delimiter,
                expectedMessageStrs.toArray(new String[0]));
    }

    public static void compareMessages(List<Message> actualMessages,
                                       char delimiter, String... expectedMessageStrs)
            throws InvalidMessage {
        checkArgument(expectedMessageStrs.length == actualMessages.size(), "Number of message mistach");

        for (int i = 0; i < expectedMessageStrs.length; i++) {
            Message actualMessage = actualMessages.get(i);
            Message expectedMessage = FixMessageUtil.parse(
                    expectedMessageStrs[i], delimiter);
            Pair<Integer, String> firstDiff = include(actualMessage,
                    expectedMessage);
            if (firstDiff != null) {
                throw new IllegalStateException("Message different: " + firstDiff + ", "
                        + actualMessage);
            }
        }
    }

    public static Pair<Integer, String> include(Message message1,
                                                Message message2) {
        return include(message1, message2, excludeTags);
    }

    public static Pair<Integer, String> include(Message message1,
                                                Message message2, int[] excludeTags) {
        List<Pair<Integer, String>> tuples1 = parse(message1.toString());
        List<Pair<Integer, String>> tuples2 = parse(message2.toString());

        return include(tuples1, tuples2, excludeTags);
    }

    private static Pair<Integer, String> include(
            List<Pair<Integer, String>> tuples1,
            List<Pair<Integer, String>> tuples2, int[] excludeTags) {
        Set<Integer> excludes = new HashSet<Integer>();
        for (int tag : excludeTags) {
            excludes.add(tag);
        }

        for (Pair<Integer, String> tuple2 : tuples2) {
            if (!excludes.contains(tuple2.getLeft())
                    && !tuples1.contains(tuple2)) {
                return tuple2;
            }
        }
        return null;
    }

    private static List<Pair<Integer, String>> parse(String text) {
        String[] ss = text.split(SEPARATOR);

        List<Pair<Integer, String>> result = new LinkedList<Pair<Integer, String>>();
        for (String s : ss) {
            int index = s.indexOf('=');
            int tag = Integer.parseInt(s.substring(0, index));
            String value = s.substring(index + 1);
            Pair<Integer, String> t = Pair.of(tag, value);
            result.add(t);
        }
        return result;
    }
}
