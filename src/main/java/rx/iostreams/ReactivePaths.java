package rx.iostreams;

import org.reactivestreams.Publisher;

import java.nio.file.Path;

public final class ReactivePaths {

    private ReactivePaths() {

    }

    public static Publisher<String> publishLines(Path path) {
        return new LinePublisher(path);
    }
}
