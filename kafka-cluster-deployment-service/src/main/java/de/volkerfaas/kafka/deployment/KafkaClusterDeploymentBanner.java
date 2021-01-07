package de.volkerfaas.kafka.deployment;

import de.volkerfaas.kafka.deployment.utils.ImplementationEntries;
import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.Arrays;

public class KafkaClusterDeploymentBanner implements Banner {

    private static final String[] BANNER = {
            " _  __          __   _               ___   _               _               ",
            "| |/ /  __ _   / _| | |__  __ _     / __| | |  _  _   ___ | |_   ___   _ _ ",
            "| ' <  / _` | |  _| | / / / _` |   | (__  | | | || | (_-< |  _| / -_) | '_|",
            "|_|\\_\\ \\__,_| |_|   |_\\_\\ \\__,_|    \\___| |_|  \\_,_| /__/  \\__| \\___| |_|  ",
            "       ___                 _                                     _   ",
            "      |   \\   ___   _ __  | |  ___   _  _   _ __    ___   _ _   | |_ ",
            "      | |) | / -_) | '_ \\ | | / _ \\ | || | | '  \\  / -_) | ' \\  |  _|",
            "      |___/  \\___| | .__/ |_| \\___/  \\_, | |_|_|_| \\___| |_||_|  \\__|",
            "===================|_|===============|__/=================================="
    };

    private static final int STRAP_LINE_SIZE = 75;

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        Arrays.stream(BANNER).forEach(out::println);
        final ImplementationEntries implementationEntries = ImplementationEntries.get(Application.class);
        final String vendor = String.format(" :: by %s :: ", implementationEntries.getVendor("unknown"));
        final String version = String.format("(%s)", implementationEntries.getVersion("X.X-SNAPSHOT"));
        final StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + vendor.length())) {
            padding.append(" ");
        }
        out.println(AnsiOutput.toString(AnsiColor.BLUE, vendor, padding.toString(), version));
        out.println();
    }

}
