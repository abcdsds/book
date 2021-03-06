package book.modules.comment;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import book.modules.account.Account;
import book.modules.post.Post;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> , CommentRepositoryExtension {

	List<Comment> findAllByPostAndDeleted(Post post, boolean b);

	@EntityGraph(attributePaths = {"parent","group","childList"})
	List<Comment> findAllByPostAndDeletedOrderByIdAsc(Post post, boolean b);

	@EntityGraph("Comment.withParentAndGroupAndChildList")
	List<Comment> findAllByPostAndDeletedAndDepthOrderByIdAsc(Post post, boolean b, int i);

	@EntityGraph("Comment.withParentAndGroupAndChildList")
	List<Comment> findAllByPostAndDepthOrderByIdAsc(Post post, int i);
	
	int countByPost(Post post);
	
	int countByPostAndDeleted(Post post, boolean deleted);

	Comment findTopByOrderByIdDesc();
	
	List<Comment> findTop10ByOrderByIdDesc();

	@EntityGraph(attributePaths = "createdBy")
	Comment findCommentById(Long id);

	@EntityGraph(attributePaths = "post")
	List<Comment> findAllByCreatedByAndDeleted(Account account, boolean b);
}
