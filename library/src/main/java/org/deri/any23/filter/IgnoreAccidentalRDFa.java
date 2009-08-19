package org.deri.any23.filter;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.rdfa.RDFaExtractor;
import org.deri.any23.vocab.XHTML;
import org.deri.any23.writer.TripleHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * A {@link TripleHandler} that suppresses output of the RDFa
 * parser if the document only contains "accidental" RDFa,
 * like stylesheet links and other non-RDFa uses of HTML's
 * @@rel and @@rev attributes.
 *   
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class IgnoreAccidentalRDFa implements TripleHandler {
	private final ExtractionContextBlocker blocker;
	
	public IgnoreAccidentalRDFa(TripleHandler wrapped) {
		this.blocker = new ExtractionContextBlocker(wrapped);
	}
	
	@Override
	public void startDocument(URI documentURI) {
		blocker.startDocument(documentURI);
	}
	
	@Override
	public void openContext(ExtractionContext context) {
		blocker.openContext(context);
		if (isRDFaContext(context)) {
			blocker.blockContext(context);
		}
	}

	@Override
	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		if (isRDFaContext(context) && !p.stringValue().startsWith(XHTML.NS)) {
			blocker.unblockContext(context);
		}
		blocker.receiveTriple(s, p, o, context);
	}

	@Override
	public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
		blocker.receiveNamespace(prefix, uri, context);
	}
	
	@Override
	public void closeContext(ExtractionContext context) {
		blocker.closeContext(context);
	}
	
	@Override
	public void close() {
		blocker.close();
	}

	private boolean isRDFaContext(ExtractionContext context) {
		return context.getExtractorName().equals(RDFaExtractor.NAME);
	}

	@Override
	public void endDocument(URI documentURI) {
		blocker.endDocument(documentURI);		
	}
	
	@Override
	public void setContentLength(long contentLength) {
//		_contentLength = contentLength;
		//ignore
		;
	}
}