package org.matsim.project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.scenario.ScenarioUtils;

import org.matsim.counts.Counts;

import org.matsim.vehicles.Vehicle;



public class RunMatsim {
	
		


	public void run_MATSim(String path) throws Exception, IOException{
		
		double speedfactor [] = {0.913819888,0.907529595,0.905589126,0.908653448,0.920459311,0.906343947,0.738518096,0.650561535,0.630794439,0.678957403,0.724268665,0.750880124,0.753074063,0.734076102,0.685970047,0.638554922,0.613693656,0.595692115,0.626505016,0.711358501,0.799820253,0.898303461,0.912174085,0.911181505};
		double ExpressFactor []= {0.443784647,0.462896104,0.488964576,0.451651106,1.627747433};
		double ArterialFactor [] = {0.8, 0.8, 0.8, 0.8, 0.8};



		
				Config config = ConfigUtils.loadConfig(path);
				config.network().setTimeVariantNetwork(true);
				config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
				
				
				Scenario scenario = ScenarioUtils.loadScenario(config) ;
				
				Controler controler = new Controler( scenario ) ;

				for ( Link link : scenario.getNetwork().getLinks().values() ) {
					double speed = link.getFreespeed() ;
					//final double threshold = 5./3.6;
					double capacity = link.getCapacity() ;
					Set<String> linkType = link.getAllowedModes();
	
					if(linkType.contains("car")){
						if ( speed > 33 ) {
							for(int i = 0; i < 24; i++){
								NetworkChangeEvent event = new NetworkChangeEvent(i*3600.) ;
								event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed*speedfactor[i] ));
								if(i < 6) {
									event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[4] ));
								}else if(i < 9 && i >= 6) {
									event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[0] ));
								}else if(i < 15 && i >= 9) {
									event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[1] ));
								}else if(i < 19 && i >= 15) {
									event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[2] ));
								}else if(i < 21 && i >= 19) {
									event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[3] ));
								}else if(i >= 21) {
									event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/3600*ExpressFactor[4] ));
								}
								event.addLink(link);
								NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
							}
//							
						}
						
					}
				}
				controler.run();
					}
					
				
				// ---
//				controler.addOverridingModule(new SwissRailRaptorModule());
//				controler.addOverridingModule(new SBBTransitModule());
//				controler.configureQSimComponents(components -> {
//					SBBTransitEngineQSimModule.configure(components);
//				});
				
				
				
				
				
	public static void main(String[] args) {
			RunMatsim r = new RunMatsim();
			try {
				r.run_MATSim("");  //path to the config file
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
