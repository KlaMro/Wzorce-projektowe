interface Power {
    int get();
}

class Power230V {
    public int get230V() {
        return 230;
    }
}

class Power110V {
    public int get110V() {
        return 110;
    }
}

class PowerAdapter230V implements Power {
    private Power230V power230V;

    public PowerAdapter230V(Power230V power230V) {
        this.power230V = power230V;
    }

    @Override
    public int get() {
        return power230V.get230V() / 46;
    }
}

class PowerAdapter110V implements Power {
    private Power110V power110V;

    public PowerAdapter110V(Power110V power110V) {
        this.power110V = power110V;
    }

    @Override
    public int get() {
        return power110V.get110V() / 22;
    }
}

class Device {
    private Power power;

    public Device(Power power) {
        this.power = power;
    }

    public void run() {
        System.out.println("Urządzenie działa - napięcie " + power.get() + "V");
    }
}

interface TemperatureSensor {
    double getTemperatureInCelsius();
}

class KelvinSensor {
    public double getTemperatureInKelvin() {
        return 373;
    }
}

class FahrenheitSensor {
    public double getTemperatureInFahrenheit() {
        return 100;
    }
}

class KelvinToCelsiusAdapter implements TemperatureSensor {
    private KelvinSensor kelvinSensor;

    public KelvinToCelsiusAdapter(KelvinSensor kelvinSensor) {
        this.kelvinSensor = kelvinSensor;
    }

    @Override
    public double getTemperatureInCelsius() {
        return kelvinSensor.getTemperatureInKelvin() - 273.15;
    }
}

class FahrenheitToCelsiusAdapter implements TemperatureSensor {
    private FahrenheitSensor fahrenheitSensor;

    public FahrenheitToCelsiusAdapter(FahrenheitSensor fahrenheitSensor) {
        this.fahrenheitSensor = fahrenheitSensor;
    }

    @Override
    public double getTemperatureInCelsius() {
        return (fahrenheitSensor.getTemperatureInFahrenheit() - 32) * 5 / 9;
    }
}

class TemperatureApp {
    private TemperatureSensor sensor;

    public TemperatureApp(TemperatureSensor sensor) {
        this.sensor = sensor;
    }

    public void showTemperature() {
        System.out.println("Temperatura: " + sensor.getTemperatureInCelsius() + "°C");
    }
}

public class MyApp11 {
    public static void main(String[] args) {
        Power230V power230V = new Power230V();
        Power adapter230 = new PowerAdapter230V(power230V);
        Power110V power110V = new Power110V();
        Power adapter110 = new PowerAdapter110V(power110V);

        Device device1 = new Device(adapter230);
        device1.run();
        Device device2 = new Device(adapter110);
        device2.run();

        KelvinSensor kelvinSensor = new KelvinSensor();
        TemperatureSensor kelvinAdapter = new KelvinToCelsiusAdapter(kelvinSensor);
        FahrenheitSensor fahrenheitSensor = new FahrenheitSensor();
        TemperatureSensor fahrenheitAdapter = new FahrenheitToCelsiusAdapter(fahrenheitSensor);
        TemperatureApp app1 = new TemperatureApp(kelvinAdapter);
        app1.showTemperature();
        TemperatureApp app2 = new TemperatureApp(fahrenheitAdapter);
        app2.showTemperature();
    }
}


