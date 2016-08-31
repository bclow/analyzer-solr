package analyzer.solr5.jieba;

import com.huaban.analysis.jieba.SegToken;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.Reader;

public class JiebaTokenizer extends Tokenizer {
	private CharTermAttribute termAtt;
	private OffsetAttribute offsetAtt;
	private TypeAttribute typeAtt;
	private int endPosition;
	
	
	private JiebaAdapter jieba;

	protected JiebaTokenizer(String segModeName, String userDictPath) {
		
	    this.offsetAtt = addAttribute(OffsetAttribute.class);
	    this.termAtt = addAttribute(CharTermAttribute.class);
	    this.typeAtt = addAttribute(TypeAttribute.class);
	    
	    jieba = new JiebaAdapter(input, segModeName, userDictPath);
	    
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
        while(jieba.hasNext()) {
			SegToken token = jieba.next();
            if(token.word.getToken().equals(" ")) {
                continue;
            }
//			termAtt.append(token.word.getToken());
			termAtt.append(token.word);
			termAtt.setLength(token.word.length());
			offsetAtt.setOffset(token.startOffset, token.endOffset);
			endPosition = token.endOffset;
			typeAtt.setType(token.word.getTokenType());			
			return true;
		}
		return false;
	}

	@Override
	public void end() throws IOException {
		int finalOffset = correctOffset(this.endPosition);
		offsetAtt.setOffset(finalOffset, finalOffset);
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		jieba.reset(this.input);
	}
}
