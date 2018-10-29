close('all');
restoredefaultpath;path(path,'C:\Users\Nathan\Documents\NetBeansProjects\gravsim');

objectIDs = [47,34];
dataFilePath = '.\simLog.txt';
dataFilePathCoarse = '.\simLog_Coarse.txt';
simFilePath = '.\population_20290.txt';

G = (6.67408E-11)*(1/1000)^3;

vecMag = @(v) sqrt(v(:,1).^2 + v(:,2).^2);
% vecDiff = @(a,b) sqrt((a(:,1)-b(:,1)).^2 + (a(:,2)-b(:,2)).^2);

if ~exist('orbitData','var')
    fprintf(1,'Load %s ',dataFilePath); tic;
    orbitData = readSimLog(dataFilePath);
    fprintf(1,'(%.1f)\n',toc);
    
    fprintf(1,'Load %s ',dataFilePathCoarse); tic;
    orbitDataCoarse = readSimLog(dataFilePathCoarse);
    fprintf(1,'(%.1f)\n',toc);
else
    fprintf(1,'Skipping Loading Data\n');
end
simData = loadSim(simFilePath);

aSpikeData.normThreshold = 2E9;
aSpikeData.Indicies = find(orbitData.aMagNorm > aSpikeData.normThreshold);
fprintf(1,'%d Normalized a Spikes of > %.2E\n',size(aSpikeData.Indicies,1),aSpikeData.normThreshold);

%plot distribution of acceleration spikes
allIDs = unique(orbitData.id);
figure(1);
subplot(3,1,1);
for i = 1:size(allIDs,1)
    theseIndicies = orbitData.id == allIDs(i);
    plot(orbitData.time(theseIndicies),orbitData.aMag(theseIndicies,1),'-'); hold('on');
end
plot(orbitData.time(aSpikeData.Indicies),orbitData.aMag(aSpikeData.Indicies),'s','Color','red');
hold('off');
xlabel('time'); ylabel('a mag');
grid('on');
subplot(3,1,2);
for i = 1:size(allIDs,1)
    theseIndicies = orbitData.id == allIDs(i);
    plot(orbitData.time(theseIndicies),orbitData.aMagNorm(theseIndicies),'-'); hold('on');
end
plot(orbitData.time(aSpikeData.Indicies),orbitData.aMagNorm(aSpikeData.Indicies),'s','Color','red');
hold('off');
xlabel('time'); ylabel('a mag * r^2');
grid('on');
subplot(3,1,3)
hist(orbitData.aMag);
grid('on');
xlabel('A Mag');

timeLimits = [min(orbitData.time),max(orbitData.time)];

figure(2)
thisIndexA = orbitData.id == objectIDs(1);
positionsA = orbitData.p(thisIndexA,:);
thisIndexB = orbitData.id == objectIDs(2);
positionsB = orbitData.p(thisIndexB,:);
plot(0,0,'o','Color','blue'); hold('on');
plot(positionsB(:,1)-positionsA(:,1),positionsB(:,2)-positionsA(:,2),'-o','Color','red');

timeIndexCoarse = timeLimits(1) <= orbitDataCoarse.time & orbitDataCoarse.time <= timeLimits(2);
thisIndexACoarse = orbitDataCoarse.id == objectIDs(1) & timeIndexCoarse;
positionsACoarse = orbitDataCoarse.p(thisIndexACoarse,:);
thisIndexBCoarse = orbitDataCoarse.id == objectIDs(2) & timeIndexCoarse;
positionsBCoarse = orbitDataCoarse.p(thisIndexBCoarse,:);
% plot(positionsACoarse(:,1)-positionsA(:,1),positionsACoarse(:,2)-positionsA(:,2),'-','Color','blue');
plot(positionsBCoarse(:,1)-positionsACoarse(:,1),positionsBCoarse(:,2)-positionsACoarse(:,2),'-x','Color','black');
hold('off');
title(sprintf('Time %.0f to %.0f: Objects %d, %d',timeLimits(1),timeLimits(2),objectIDs(1),objectIDs(2)));
grid('on');

figure(3)
plot(positionsA(:,1),positionsA(:,2),'-o','Color','blue'); hold('on');
plot(positionsB(:,1),positionsB(:,2),'-o','Color','red');
plot(positionsACoarse(:,1),positionsACoarse(:,2),'-x','Color','blue');
plot(positionsBCoarse(:,1),positionsBCoarse(:,2),'-x','Color','red');
hold('off');
title(sprintf('Time %.0f to %.0f: Objects %d, %d',timeLimits(1),timeLimits(2),objectIDs(1),objectIDs(2)));
grid('on');












