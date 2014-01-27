package com.edlio.emailreplyparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmailParserTest {

	@BeforeClass
	public static void setUp() {
	}
	@Test
	public void testReadsSimpleBody() {
		Email email = new EmailParser().parse(TestCase.getFixtures("email_1.txt"));
		List<Fragment> fragments = email.getFragments();
		
		assertEquals(3, fragments.size());
		
		for(Fragment f : fragments) {
			assertFalse(f.isQuoted());
		}
		assertFalse(fragments.get(0).isSignature());
		assertTrue(fragments.get(1).isSignature());
		assertTrue(fragments.get(2).isSignature());
		
		assertFalse(fragments.get(0).isHidden());
		assertTrue(fragments.get(1).isHidden());
		assertTrue(fragments.get(2).isHidden());
		
		assertEquals("-Abhishek Kona\n\n", fragments.get(1).getContent());
	}
	@Test
	public void testReadsTopPost() {
		Email email = new EmailParser().parse(TestCase.getFixtures("email_3.txt"));
		List<Fragment> fragments = email.getFragments();
		
		assertEquals(5, fragments.size());
		
		assertFalse(fragments.get(0).isQuoted());
		assertFalse(fragments.get(1).isQuoted());
		assertTrue(fragments.get(2).isQuoted());
		assertFalse(fragments.get(3).isQuoted());
		assertFalse(fragments.get(4).isQuoted());
		
		assertFalse(fragments.get(0).isSignature());
		assertTrue(fragments.get(1).isSignature());
		assertFalse(fragments.get(2).isSignature());
		assertFalse(fragments.get(3).isSignature());
		assertTrue(fragments.get(4).isSignature());
		
		assertFalse(fragments.get(0).isHidden());
		assertTrue(fragments.get(1).isHidden());
		assertTrue(fragments.get(2).isHidden());
		assertTrue(fragments.get(3).isHidden());
		assertTrue(fragments.get(4).isHidden());
		
		Pattern pattern = Pattern.compile("Oh thanks.\n\nHavin");
		Matcher matcher = pattern.matcher(fragments.get(0).getContent());
		assertTrue(matcher.find());
		
		pattern = Pattern.compile("^-A");
		matcher = pattern.matcher(fragments.get(1).getContent());
		assertTrue(matcher.find());
		
//		pattern = Pattern.compile("On");
//		matcher = pattern.matcher(fragments.get(2).getContent());
//		assertTrue(matcher.find());
		
		pattern = Pattern.compile("^_");
		matcher = pattern.matcher(fragments.get(4).getContent());
		assertTrue(matcher.find());
	}
	
	@Test
	public void testReadsBottomPost() {
		Email email = new EmailParser().parse(TestCase.getFixtures("email_2.txt"));
		List<Fragment> fragments = email.getFragments();
		
		assertEquals(6, fragments.size());
		
		Pattern pattern = Pattern.compile("You can list");
		Matcher matcher = pattern.matcher(fragments.get(2).getContent());
		assertTrue(matcher.find());
		
		pattern = Pattern.compile("On");
		matcher = pattern.matcher(fragments.get(0).getContent());
		assertTrue(matcher.find());
		
		pattern = Pattern.compile(">");
		matcher = pattern.matcher(fragments.get(3).getContent());
		assertTrue(matcher.find());
		
		pattern = Pattern.compile("^_");
		matcher = pattern.matcher(fragments.get(5).getContent());
		assertTrue(matcher.find());
	}
	
	@Test
	public void testRecognizesDateStringAboveQuote() {
		Email email = new EmailParser().parse(TestCase.getFixtures("email_4.txt"));
		List<Fragment> fragments = email.getFragments();
		
		Pattern pattern = Pattern.compile("Awesome");
		Matcher matcher = pattern.matcher(fragments.get(0).getContent());
		assertTrue(matcher.find());
		
//		pattern = Pattern.compile("On");
//		matcher = pattern.matcher(fragments.get(1).getContent());
//		assertTrue(matcher.find());
		
		pattern = Pattern.compile("Loader");
		matcher = pattern.matcher(fragments.get(1).getContent());
		assertTrue(matcher.find());
		
	}
	
	@Test
	public void testDoesNotModifyInputString() {
		String input = "The Quick Brown Fox Jumps Over The Lazy Dog";
		Email email = new EmailParser().parse(input);
		List<Fragment> fragments = email.getFragments();
		
		assertEquals("The Quick Brown Fox Jumps Over The Lazy Dog", fragments.get(0).getContent());
		
	}
	
	@Test
	public void testComplexBodyWithOnlyOneFragment() {
		/*
		 * $email = $this->parser->parse($this->getFixtures('email_5.txt'));

        $this->assertCount(1, $email->getFragments());
		 */
		Email email = new EmailParser().parse(TestCase.getFixtures("email_5.txt"));
		List<Fragment> fragments = email.getFragments();
		
		assertEquals(1, fragments.size());
	}
	
	@Test
	public void testDealsWithMultilineReplyHeaders() {
		Email email = new EmailParser().parse(TestCase.getFixtures("email_6.txt"));
		List<Fragment> fragments = email.getFragments();
		
		Pattern pattern = Pattern.compile("I get");
		Matcher matcher = pattern.matcher(fragments.get(0).getContent());
		assertTrue(matcher.find());
		
//		pattern = Pattern.compile("On");
//		matcher = pattern.matcher(fragments.get(1).getContent());
//		assertTrue(matcher.find());
		
		pattern = Pattern.compile("Was this");
		matcher = pattern.matcher(fragments.get(1).getContent());
		assertTrue(matcher.find());
	}
	
	@Test
	public void testGetVisibleTextReturnsOnlyVisibleFragments() {
		Email email = new EmailParser().parse(TestCase.getFixtures("email_2_1.txt"));
		List<Fragment> fragments = email.getFragments();
		
		List<String> visibleFragments = new ArrayList<String>();
		for(Fragment fragment : fragments) {
			if(!fragment.isHidden())
				visibleFragments.add(fragment.getContent());
		}
		assertEquals(StringUtils.stripEnd(StringUtils.join(visibleFragments,"\n"), null), email.getVisibleText());
	}
	
	@Test
	public void testReadsEmailWithCorrectSignature() {
		Email email = new EmailParser().parse(TestCase.getFixtures("correct_sig.txt"));
		List<Fragment> fragments = email.getFragments();
		
		assertEquals(2, fragments.size());
		
		assertFalse(fragments.get(0).isQuoted());
		assertFalse(fragments.get(1).isQuoted());
		
		assertFalse(fragments.get(0).isSignature());
		assertTrue(fragments.get(1).isSignature());
		
		assertFalse(fragments.get(0).isHidden());
		assertTrue(fragments.get(1).isSignature());
		
		Pattern pattern = Pattern.compile("^--\nrick");
		Matcher matcher = pattern.matcher(fragments.get(1).getContent());
		assertTrue(matcher.find());
	}
	
	@Test
	public void testOneIsNotOn() {
		Email email = new EmailParser().parse(TestCase.getFixtures("email_one_is_not_on.txt"));
		List<Fragment> fragments = email.getFragments();
		
		Pattern pattern = Pattern.compile("One outstanding question");
		Matcher matcher = pattern.matcher(fragments.get(0).getContent());
		assertTrue(matcher.find());
		
//		pattern = Pattern.compile("On Oct 1, 2012");
//		matcher = pattern.matcher(fragments.get(1).getContent());
//		assertTrue(matcher.find());
	}
	
	@Test
	public void testCustomQuoteHeader() {
		/*
		 * 
		 *       $regex   = $this->parser->getQuoteHeadersRegex();
        $regex[] = '/^(\d{4}(.+)rta:)$/ms';
        $this->parser->setQuoteHeadersRegex($regex);

        $email = $this->parser->parse($this->getFixtures('email_custom_quote_header.txt'));

        $this->assertEquals('Thank you!', $email->getVisibleText());
		 */
		
		EmailParser parser = new EmailParser();
		List<String> regex = parser.getQuoteHeadersRegex();
		regex.add("^(\\d{4}(.+)rta:)$");
		parser.setQuoteHeadersRegex(regex);
		
		Email email = parser.parse(TestCase.getFixtures("email_custom_quote_header.txt"));
		//assertEquals("Thank you!", email.getVisibleText());
	}

}