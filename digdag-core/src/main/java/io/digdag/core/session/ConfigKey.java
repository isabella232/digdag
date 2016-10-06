package io.digdag.core.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ConfigKey
{
    private static Pattern VALID_NAME = Pattern.compile("[a-zA-Z0-9_]+");
    private static Pattern VALID_EXPRESSION = Pattern.compile("[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*");

    @JsonCreator
    public static ConfigKey parse(String expr)
    {
        if (!VALID_EXPRESSION.matcher(expr).matches()) {
            throw new IllegalArgumentException("Config key expression is invalid. Currently, only [a-zA-Z0-9_] characters are supported: " + expr);
        }

        String[] names = expr.split("\\.", -1);

        return new ConfigKey(ImmutableList.copyOf(names));
    }

    private List<String> names;

    public ConfigKey(List<String> names)
    {
        Preconditions.checkArgument(names.size() >= 1, "Number of names must be larger than 1");
        for (String key : names) {
            Preconditions.checkArgument(VALID_NAME.matcher(key).matches(), "Invalid name in keys");
        }
        this.names = ImmutableList.copyOf(names);
    }

    public List<String> getNestNames()
    {
        return names.subList(0, names.size() - 1);
    }

    public String getLastName()
    {
        return names.get(names.size() - 1);
    }

    public List<String> getNames()
    {
        return names;
    }

    @Override
    @JsonValue
    public String toString()
    {
        return String.join(".", names);
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConfigKey)) {
            return false;
        }
        ConfigKey o = (ConfigKey) other;
        return Objects.equals(names, o.names);
    }

    @Override
    public int hashCode()
    {
        return names.hashCode();
    }
}
