package modelo;
import java.time.LocalDate;
import java.time.LocalTime;
public record Entrega(int id, int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {}
