package de.imise.tool3lgm.graphtools.undoredo;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CommandHandlerTest {

    final String cmd = "C";
    final String arg0 = "A";
    final String arg1 = "'Q'";
    final String arg2 = "";
    final String arg3 = "''";
    final String arg4 = "' '";
    final String arg5 = "'   '";

    final String[] args = {
            arg0, arg1, arg2, arg3, arg4, arg5
    };

    final String ret0 = arg0;
    final String ret1 = removeFirstAndLast(arg1);
    final String ret2 = arg2;
    final String ret3 = removeFirstAndLast(arg3);
    final String ret4 = removeFirstAndLast(arg4);
    final String ret5 = removeFirstAndLast(arg5);

    final String[] rets = {
            ret0, ret1, ret2, ret3, ret4, ret5
    };

    private static final String removeFirstAndLast(final String s) {
        return s.substring(1, s.length() - 1);
    }

    @Test
    public void validateTestData() {
        Assert.assertEquals(arg0, ret0);
        Assert.assertEquals(arg1, "'" + ret1 + "'");
        Assert.assertEquals(arg2, ret2);
        Assert.assertEquals(arg3, "'" + ret3 + "'");
        Assert.assertEquals(arg4, "'" + ret4 + "'");
        Assert.assertEquals(arg5, "'" + ret5 + "'");
    }

    @Test
    public void parseCommandLineNoArg() {
        runTest();
    }

    @Test
    public void parseCommandLineSimple() {
        for (int i = 0; i < args.length; i++) {
            runTest(i);
        }
    }

    @Test
    public void parseCommandLineTwoArgs() {
        for (int i = 0; i < args.length - 1; i++) {
            runTest(i, i + 1);
        }
    }

    @Test
    public void parseCommandLineTwoArgsBackward() {
        for (int i = 0; i < args.length - 1; i++) {
            runTest(i + 1, i);
        }
    }

    @Test
    public void parseCommandLineTwoArgsSameArg() {
        for (int i = 0; i < args.length; i++) {
            runTest(i, i);
        }
    }

    @Test
    public void parseCommandLineMultipleWhitespaces() {
        realTest(cmd + "  a", "", "a");
        realTest(cmd + "   ", "", "", "");
        realTest(cmd + "   ''", "", "", "");
        realTest(cmd + " '' ", "", "");
        realTest(cmd + " 'aaa' 'bbb'", "aaa", "bbb");
        realTest(cmd + " aaa bbb ", "aaa", "bbb", "");
        realTest(cmd + " '' aaa bbb ''", "", "aaa", "bbb", "");
    }

    private void realTest(final String commandLine, final String... expectedArgs) {
        List<String> args = new ArrayList<>();
        String commandName = CommandHandler.parseCommandLine(commandLine, args);
        assertTrue(commandLine.startsWith(commandName));
        assertTrue(commandLine.equals(commandName) || commandLine.charAt(commandName.length()) == ' ');
        Assert.assertEquals(args, Arrays.asList(expectedArgs));
    }

    private void runTest(final int... argumentIndex) {
        String command = getCmd(argumentIndex);
        realTest(command, getRet(argumentIndex));
    }

    private String getCmd(final int... argumentIndex) {
        StringBuilder sb = new StringBuilder(cmd);
        for (int i = 0; i < argumentIndex.length; i++) {
            sb.append(" ");
            sb.append(args[i]);
        }
        return sb.toString();
    }

    private String[] getRet(final int... returnIndex) {
        String[] returnList = new String[returnIndex.length];
        for (int i = 0; i < returnIndex.length; i++) {
            returnList[i] = rets[i];
        }
        return returnList;
    }

}
