package book.modules.post;

import java.nio.file.AccessDeniedException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.Account;
import book.modules.account.CurrentAccount;
import book.modules.board.Board;
import book.modules.board.BoardService;
import book.modules.comment.Comment;
import book.modules.comment.CommentService;
import book.modules.comment.form.CommentForm;
import book.modules.post.form.PostForm;
import book.modules.post.form.PostPrevNextForm;
import book.modules.post.vote.VoteType;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostController {

	private final PostRepository postRepository;
	private final PostService postService;
	private final CommentService commentService;
	private final ModelMapper modelMapper;
	private final ObjectMapper objectMapper;
	
	@GetMapping("/{boardName}/add")
	public String postAddForm(@CurrentAccount Account account, Model model, @PathVariable String boardName) {
		
		PostForm form = PostForm.builder().boardName(boardName).build();
		
		
		model.addAttribute(account);
		model.addAttribute(form);
		
		return "post/add";
	}
	
	@PostMapping("/add")
	public String postAddSubmit(@CurrentAccount Account account, Model model,
								@Valid PostForm form, Errors errors,
								RedirectAttributes redirect) throws AccessDeniedException {
		
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return "post/add";
		}
		
		postService.add(account, form);
		
		return "redirect:/board/"+ form.getBoardName();
	}
	
// 	comment 에서 댓글목록 따로 출력
//	@GetMapping("/view/{id}")
//	public String view(@CurrentAccount Account account, Model model, @PathVariable Long id, HttpServletResponse response,HttpServletRequest request) throws NotFoundException {
//	
//		Post post = postService.getPost(id,response,request);
//		PostPrevNextForm prevNextId = postService.getPrevNextId(id);
//		List<Comment> commentList = commentService.getCommentList(post);
//		//System.out.println(commentList.size());
//		System.out.println("===========SIZE=========");
//		// commentList.sort((a,b) -> a.getId().compareTo(b.getId())); //정렬 오름차순
//		
//		
//		model.addAttribute("post" , post);
//		model.addAttribute(new CommentForm());
//		model.addAttribute(prevNextId);
//		model.addAttribute("commentList", commentList);
//		model.addAttribute("commentTotalCount" , commentService.getTotalCount(post));
//		model.addAttribute(account);
//		
//		return "post/view_CommentVersion";
//	}
	
	
	@GetMapping("/view/{id}")
	public String view(@CurrentAccount Account account, Model model, @PathVariable Long id, HttpServletResponse response,HttpServletRequest request) throws NotFoundException {
	
		Post post = postService.getPost(id,response,request);
		PostPrevNextForm prevNextId = postService.getPrevNextId(id);
		
		model.addAttribute("post" , post);
		model.addAttribute(new CommentForm());
		model.addAttribute(prevNextId);
		model.addAttribute("commentTotalCount" , commentService.getTotalCount(post));
		model.addAttribute(account);
		
		return "post/view";
	}
	
	@PostMapping("/vote/{type}")
	@ResponseBody
	public ResponseEntity<String> postUp(@CurrentAccount Account account, Model model, @PathVariable("type") VoteType type , Long postId) throws JsonProcessingException, NotFoundException {
	
		
		String message = postService.vote(account, postId, type);
		
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
		
	
	
	@GetMapping("/update/{id}")
	public String updateForm(@CurrentAccount Account account, Model model, @PathVariable Long id) throws AccessDeniedException {
				
		Post post = postService.getPostWithAccount(id,account,false);
		PostForm map = modelMapper.map(post, PostForm.class);
		
		model.addAttribute(map);
		model.addAttribute(account);
		model.addAttribute("post_id" , id);
		
		return "post/update";
	}
	
	@PostMapping("/update/{id}")
	public String updateSubmit(@CurrentAccount Account account, Model model,
								@Valid PostForm form, Errors errors,
								RedirectAttributes redirect, @PathVariable Long id) throws AccessDeniedException {
		
		if (errors.hasErrors()) {
			return "redirect:/post/update/"+id;
		}
		
		Post post = postService.getPostWithAccount(id,account,false);
		postService.updatePost(post , form);
		
		return "redirect:/post/view/"+id;
	}
	
	@PostMapping("/delete")
	@ResponseBody
	public ResponseEntity<String> deletePost(@CurrentAccount Account account, Model model, Long postId) throws AccessDeniedException, JsonProcessingException {
		
		String message = postService.UpdateDeleteStatus(postId,account,false);
		
		return new ResponseEntity<>(message , HttpStatus.OK);
	}
}
