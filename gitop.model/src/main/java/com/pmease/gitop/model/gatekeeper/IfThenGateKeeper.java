package com.pmease.gitop.model.gatekeeper;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.pmease.commons.editable.annotation.Editable;
import com.pmease.commons.editable.annotation.TableLayout;
import com.pmease.gitop.model.PullRequest;
import com.pmease.gitop.model.gatekeeper.checkresult.Accepted;
import com.pmease.gitop.model.gatekeeper.checkresult.BlockedPending;
import com.pmease.gitop.model.gatekeeper.checkresult.CheckResult;
import com.pmease.gitop.model.gatekeeper.checkresult.Rejected;

@SuppressWarnings("serial")
@Editable(name="Check Second Gate Keeper If First Gate Keeper Is Passed", order=300, icon="icon-servers",  
		description="If first gate keeper is passed, go ahead to check second gate keeper; otherwise, consider "
				+ "the whole gate keeper as passed.")
@TableLayout
public class IfThenGateKeeper extends CompositeGateKeeper {

	private GateKeeper ifGate = new DefaultGateKeeper();
	
	private GateKeeper thenGate = new DefaultGateKeeper();
	
	@Valid
	@NotNull
	public GateKeeper getIfGate() {
		return ifGate;
	}

	public void setIfGate(GateKeeper ifGate) {
		this.ifGate = ifGate;
	}

	@Valid
	@NotNull
	public GateKeeper getThenGate() {
		return thenGate;
	}

	public void setThenGate(GateKeeper thenGate) {
		this.thenGate = thenGate;
	}

	@Override
	public CheckResult doCheck(PullRequest request) {
		CheckResult ifResult = getIfGate().check(request);
		if (ifResult instanceof Accepted) {
			return getThenGate().check(request);
		} else if (ifResult instanceof Rejected) {
			return accepted(ifResult.getReasons());
		} else if (ifResult instanceof BlockedPending) {
			return ifResult;
		} else {
			CheckResult thenResult = getThenGate().check(request);
			if (thenResult instanceof Accepted)
				return thenResult;
			else 
				return ifResult;
		}
	}

}
