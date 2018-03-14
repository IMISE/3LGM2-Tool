package de.imise.tool3lgm.graphtools.undoredo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CommandParserTest {

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

    final int cLength = cmd.length() + 1;

    private static final String removeFirstAndLast(final String s) {
        return s.substring(1, s.length() - 1);
    }

    @BeforeTest
    public void beforeTest() {
    }

    @AfterTest
    public void afterTest() {
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
    public void getArgumentsNoArg() {
        realTest();
    }

    @Test
    public void getArgumentsSimple() {
        for (int i = 0; i < args.length; i++) {
            realTest(i);
        }
    }

    @Test
    public void getArgumentsTwoArgs() {
        for (int i = 0; i < args.length - 1; i++) {
            realTest(i, i + 1);
        }
    }

    @Test
    public void getArgumentsMultipleWhitespaces() {
        //2 Whitespaces zwischen cmd und erstem arg
        String command = cmd + "  a";
        List<String> arguments = CommandParser.getArguments(command, cLength);
        List<String> expected = Arrays.asList("", "a");
        Assert.assertEquals(arguments, expected);
    }

    @Test
    public void getArgumentsMultipleWhitespaces2() {
        //3 Whitespaces -> = 3 leere Parameter
        String command = cmd + "   ";
        List<String> arguments = CommandParser.getArguments(command, cLength);
        List<String> expected = Arrays.asList("", "", "");
        Assert.assertEquals(arguments, expected);
    }

    @Test
    public void getArgumentsMultipleWhitespaces3() {
        //3 Whitespaces und 1 x leere Quotes -> = 3 leere Parameter
        String command = cmd + "   ''";
        List<String> arguments = CommandParser.getArguments(command, cLength);
        List<String> expected = Arrays.asList("", "", "");
        Assert.assertEquals(arguments, expected);
        //1 x leere Quotes und 3 Whitespaces-> = 3 leere Parameter
        command = cmd + " '' ";
        arguments = CommandParser.getArguments(command, cLength);
        expected = Arrays.asList("", "");
        Assert.assertEquals(arguments, expected);
        command = cmd + " 'aaa' 'bbb'";
        arguments = CommandParser.getArguments(command, cLength);
        expected = Arrays.asList("aaa", "bbb");
        Assert.assertEquals(arguments, expected);
        command = cmd + " aaa bbb ";
        arguments = CommandParser.getArguments(command, cLength);
        expected = Arrays.asList("aaa", "bbb", "");
        Assert.assertEquals(arguments, expected);
        command = cmd + " '' aaa bbb ''";
        arguments = CommandParser.getArguments(command, cLength);
        expected = Arrays.asList("", "aaa", "bbb", "");
        Assert.assertEquals(arguments, expected);
    }

    private void realTest(final int... argumentIndex) {
        String command = getCmd(argumentIndex);
        List<String> arguments = CommandParser.getArguments(command, cLength);
        List<String> expected = getRet(argumentIndex);
        Assert.assertEquals(arguments, expected);
    }

    private String getCmd(final int... argumentIndex) {
        StringBuilder sb = new StringBuilder(cmd);
        for (int i = 0; i < argumentIndex.length; i++) {
            sb.append(" ");
            sb.append(args[i]);
        }
        return sb.toString();
    }

    private List<String> getRet(final int... returnIndex) {
        List<String> returnList = new ArrayList<>();
        for (int i = 0; i < returnIndex.length; i++) {
            returnList.add(rets[i]);
        }
        return returnList;
    }

}
