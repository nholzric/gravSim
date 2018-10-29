close('all');

dataFilePath = '.\simLog.txt';
simFilePath = '.\gravPopulation.txt';

G = (6.67408E-11)*(1/1000)^3;

vecMag = @(v) sqrt(v(:,1).^2 + v(:,2).^2);
% vecDiff = @(a,b) sqrt((a(:,1)-b(:,1)).^2 + (a(:,2)-b(:,2)).^2);

if ~exist('orbitData','var')
    fprintf(1,'Load %s ',dataFilePath); tic;
    orbitData = readSimLog(dataFilePath);
    fprintf(1,'(%.1f)\n',toc);
else
    fprintf(1,'Skipping Loading Data\n');
end
simData = loadSim(simFilePath);

aSpikeData.normThreshold = 2E8;
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

%plot overall scene of each acceleration spike
aSpikeData.distances = nan(size(aSpikeData.Indicies,1),size(allIDs,1),1);
for i = 1:size(aSpikeData.Indicies,1)
    thisID = orbitData.id(aSpikeData.Indicies(i));
    thisTime = orbitData.time(aSpikeData.Indicies(i));
    timeIndex = orbitData.time == thisTime;
    thisPositionA = orbitData.p(timeIndex & orbitData.id == thisID,:);
    
    
    
    for j = 1:size(allIDs,1)
        if thisID == allIDs(j); continue; end
        thisPositionB = orbitData.p(timeIndex & orbitData.id == allIDs(j),:);
        aSpikeData.distances(i,j) = vecMag(thisPositionA - thisPositionB);
%         fprintf(1,'%d to %d is %.2E\n',thisID,allIDs(j),aSpikeData.distances(i,j));
    end
%     figure();
%     subplot(3,3,[1,2,3,4,5,6]);
%     plot(orbitData.p(timeIndex,1),orbitData.p(timeIndex,2),'o'); hold('on');
%     plot(orbitData.p(aSpikeData.Indicies(i),1),orbitData.p(aSpikeData.Indicies(i),2),'x','Color','red'); hold('off');
%     grid('on');
%     title(sprintf('%.0f seconds : Object %d',thisTime,thisID));
%     subplot(3,3,[7,8,9]);
%     hist(log(aSpikeData.distances(i,:)));
end

passDuration = 1*60*60; %1 hr in seconds
timeSampleRate = 60; %1 minute
for i = 1:size(aSpikeData.Indicies,1)
    %find the IDs of the two participants in conjunction
    thisIDA = orbitData.id(aSpikeData.Indicies(i));
    indexA = orbitData.id == thisIDA;
    [minDistance, thisIDBIndex] = min(aSpikeData.distances(i,:));
    thisIDB = allIDs(thisIDBIndex);
    indexB = orbitData.id == thisIDB;
    
    %find time frame of conjunction
    thisTime = orbitData.time(aSpikeData.Indicies(i));
    timeBounds = thisTime + 0.5*[-passDuration,passDuration];
    timeIndex = timeBounds(1) <= orbitData.time & orbitData.time <= timeBounds(2);
    timeSteps = [timeBounds(1):timeSampleRate:timeBounds(2)]';
    
    %find positions of objects over conjunction
    positionsA = orbitData.p(indexA & timeIndex,:);
    positionsB = orbitData.p(indexB & timeIndex,:);
    positionsInterpA = [spline(orbitData.time(indexA),orbitData.p(indexA,1),timeSteps),...
        spline(orbitData.time(indexA),orbitData.p(indexA,2),timeSteps)];
    positionsInterpB = [spline(orbitData.time(indexB),orbitData.p(indexB,1),timeSteps),...
        spline(orbitData.time(indexB),orbitData.p(indexB,2),timeSteps)];
    
    %find velocities of objects over conjunction
    velocitiesA = orbitData.v(indexA & timeIndex,:);
    velocitiesB = orbitData.v(indexB & timeIndex,:);
    
    %calculate escape velocity
    simIndex = find(simData.name == thisIDB);
    M = simData.gm(simIndex);
    escapeVB = sqrt(2*G*M./vecMag(positionsB-positionsA));
    velocityB = vecMag(velocitiesB - velocitiesA);
    prctEscapeVB = velocityB./escapeVB;
    
    figure()
%     plot(positionsInterpA(:,1),positionsInterpA(:,2),'-o','Color','blue'); hold('on');
%     plot(positionsInterpB(:,1),positionsInterpB(:,2),'-o','Color','red');
%     hold('off');
    plot(0,0,'o','Color','blue'); hold('on');
    plot(positionsInterpB(:,1)-positionsInterpA(:,1),positionsInterpB(:,2)-positionsInterpA(:,2)...
        ,'-o','Color','red');
%     plot(positionsB(:,1)-positionsA(:,1),positionsB(:,2)-positionsA(:,2)...
%         ,'-x','Color','black');
    scatter(positionsB(:,1)-positionsA(:,1),positionsB(:,2)-positionsA(:,2),[],prctEscapeVB,'s','filled');
    colorbar();
    hold('off');
    title(sprintf('%.0f seconds : Object %d, %d',thisTime,thisIDA,thisIDB));
    grid('on');
    
    fprintf(1,'%.0f seconds: %d, %d\t',thisTime,thisIDA,thisIDB);
    exportPopulationFile(sprintf('.\\population_%.0f.txt',timeBounds(1)),timeBounds,simData,orbitData);
    
    theseDistances = vecMag(positionsA - positionsB);
end















