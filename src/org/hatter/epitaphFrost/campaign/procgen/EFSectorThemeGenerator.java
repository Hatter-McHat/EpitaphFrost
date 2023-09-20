package org.hatter.epitaphFrost.campaign.procgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hatter.epitaphFrost.campaign.procgen.customThemes.VerglasThemeGenerator;
import org.hatter.epitaphFrost.campaign.procgen.customThemes.VestigeThemeGenerator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.*;

//copied from RAT
public class EFSectorThemeGenerator {

	public static List<ThemeGenerator> generators = new ArrayList<ThemeGenerator>();
	
/*	static {
		generators.add(new DerelictThemeGenerator());
		generators.add(new RemnantThemeGenerator());
		generators.add(new RuinsThemeGenerator());
		generators.add(new MiscellaneousThemeGenerator());
	}*/
	
	public static void generate(ThemeGenContext context) {
		//
		//This section is used to get all the generators added

		for (ThemeGenerator gen : SectorThemeGenerator.generators)
		{
			generators.add(gen);
		}

		 

		generators.add(new VestigeThemeGenerator());
		generators.add(new VerglasThemeGenerator());
		//not sure what this does
		Global.getSector().getMemoryWithoutUpdate().set("$ef_vestige_enabled", true);
		Global.getSector().getMemoryWithoutUpdate().set("$ef_verglas_enabled", true);
		//

		//really not sure what this does
		Collections.sort(generators, new Comparator<ThemeGenerator>() {
			public int compare(ThemeGenerator o1, ThemeGenerator o2) {
				int result = o1.getOrder() - o2.getOrder();
				if (result == 0) return o1.getThemeId().compareTo(o2.getThemeId());
				return result;
			}
		});

		float totalWeight = 0f;
			for (ThemeGenerator g : generators) {
			totalWeight += g.getWeight();
			g.setRandom(StarSystemGenerator.random);
		}

			for (ThemeGenerator g : generators) {
			float w = g.getWeight();

			float f = 0f;
			if (totalWeight > 0) {
				f = w / totalWeight;
			} else {
				if (w > 0) f = 1f;
			}
			//g.setRandom(StarSystemGenerator.random);
			g.generateForSector(context, f);

			//float used = context.majorThemes.size();
			totalWeight -= w;

		}
	}
}
