package rx.iostreams;

import io.reactivex.Flowable;
import org.junit.Test;
import org.reactivestreams.Publisher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class LinePublisherTests {

    @Test
    public void test_line_publisher() throws IOException {
        Path temp = Files.createTempFile("test", "temp");
        Iterable<String> lines = Arrays.asList("line 1", "line 2", "line 3", "line 4");
        Files.write(temp, lines);

        Publisher<String> publisher = ReactivePaths.publishLines(temp);

        Flowable.fromPublisher(publisher)
                .test()
                .assertValues("line 1", "line 2", "line 3", "line 4");
    }
}
