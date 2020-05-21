package book.modules.post;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import book.modules.base.BaseEntity;
import book.modules.board.Board;
import book.modules.comment.Comment;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Entity
public class Post extends BaseEntity {

	@Id @GeneratedValue
	@Column(name = "post_id")
	private Long id;
	
	private int views;
	
	private int up;
	
	private int down;
	
	private boolean best;
	
	private boolean lock;

	private boolean deleted;
	
	@JoinColumn(name = "board_id")
	@OneToOne(fetch = FetchType.LAZY)
	private Board category;

	@OneToMany(mappedBy = "post")
	private List<Comment> comments;
	
}
