package telran.net.games;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "game")
public class Game {
	@Id
	private Long id;
	private LocalDateTime date;
	private boolean is_finished;
	private String sequence;
	
	public Game(Long id, LocalDateTime date, boolean is_finished, String sequence) {
		this.id = id;
		this.date = date;
		this.is_finished = is_finished;
		this.sequence = sequence;
	}
	
	public Game() {
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public boolean isIs_finished() {
		return is_finished;
	}

	public String getSequence() {
		return sequence;
	}

	@Override
	public String toString() {
		return "Game [id=" + id + ", date=" + date + ", is_finished=" + is_finished + ", sequence=" + sequence + "]";
	}
}
