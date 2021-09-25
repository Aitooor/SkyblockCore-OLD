package online.nasgar.skyblockcore.fetcher;

import online.nasgar.commons.util.FiniteCountdown;
import online.nasgar.commons.util.SimpleCountdown;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ServiceFetcher<T, S extends T> extends BukkitRunnable {

    private final Class<T> abstractionClass;
    private final Class<? extends T> targetClass;
    private final FiniteCountdown countdown;
    private Consumer<S> action;
    private Runnable onFail;

    public ServiceFetcher(Class<T> abstractionClass, Class<S> targetClass, int time) {
        this.abstractionClass = Objects.requireNonNull(abstractionClass);
        this.targetClass = Objects.requireNonNull(targetClass);
        this.countdown = new SimpleCountdown(time);
    }

    @Override
    public void run() {
        ServicesManager servicesManager = Bukkit.getServicesManager();
        Collection<? extends RegisteredServiceProvider<T>> registeredServiceProviders = servicesManager.getRegistrations(abstractionClass);

        for (RegisteredServiceProvider<T> registeredServiceProvider : registeredServiceProviders) {
            T provider = registeredServiceProvider.getProvider();

            if (targetClass.equals(provider.getClass())) {
                Bukkit.getLogger().log(Level.INFO, String.format( "Found provider (%s) for %s", targetClass.getName(), abstractionClass.getName()));
                if (action != null) {
                    action.accept((S) provider);
                }
                cancel();
            }
        }

        if (countdown.hasFinished()) {
            Bukkit.getLogger().log(Level.SEVERE, String.format("No provider (%s) found for %s service", targetClass.getName(), abstractionClass.getName()));
            if (onFail != null)
                onFail.run();

            cancel();
        }

        countdown.run();
    }

    public ServiceFetcher<T, S> onFound(Consumer<S> action) {
        this.action = action;
        return this;
    }

    public ServiceFetcher<T, S> onFail(Runnable onFail) {
        this.onFail = onFail;
        return this;
    }
}