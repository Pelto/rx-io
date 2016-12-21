package rx.iostreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

class LinePublisher implements Publisher<String> {

    private final Path path;

    public LinePublisher(Path path) {
        this.path = path;
    }

    @Override
    public void subscribe(Subscriber<? super String> s) {
        s.onSubscribe(new BufferedReaderSubscription(path, s));
    }

    private static class BufferedReaderSubscription extends AtomicLong implements Subscription {

        private final Path path;

        private final Subscriber<? super String> subscriber;

        private BufferedReader reader = null;

        private volatile boolean disposed = false;

        private BufferedReaderSubscription(Path path, Subscriber<? super String> subscriber) {
            this.path = path;
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n == 0 || disposed) {
                return;
            }

            if (n < 0) {
                subscriber.onError(new IllegalArgumentException("n < 0"));
                dispose();
                return;
            }

            if (reader == null) {
                try {
                    reader = Files.newBufferedReader(path);
                } catch (IOException e) {
                    subscriber.onError(e);
                    disposed = true;
                    return;
                }
            }

            String line = null;

            try {
                line = reader.readLine();
            } catch (IOException e) {
                subscriber.onError(e);
                dispose();
                return;
            }

            if (line == null) {
                subscriber.onComplete();
                dispose();
                return;
            }

            subscriber.onNext(line);

            request(n - 1);
        }

        @Override
        public void cancel() {
            dispose();
        }

        private void dispose() {
            disposed = true;
            try {
                reader.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
    }
}
