%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%         TEST ON REALISTIC DATA          %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Scenario: several weekly habit with difference reliability, variance,...
% One unit is a minut. => does not work well (20% of success).

% period = [10080 10080 10080 10080 10080 10080];
% variance = [5 10 30 60 25 15];
% noise = [8 16 4 6 20 10];
% reliability = [0.9 0.8 0.82 0.72 0.79 0.6];
% offset = 0:1440:5*1440;
% 
% test1_success = zeros(1,10);
% test1_conso = zeros(1,10);
% for i=1 : length(period)
%     for j=1 : 100
%         [finded_period,~] = scenario(period,reliability,variance,noise,10,offset);
%         if finded_period(1) == 10080
%             test1_success(i) = test1_success(i) + 1;
%         end
%         if ismember(period(i),finded_period(1:5))
%             test1_conso(i) = test1_conso(i) + 1;
%         end
%     end
% end
% 
% test1_success
% test1_conso

% %Same as previous but with random offset: => work better 
% period = [10080 10080 10080 10080 10080 10080];
% variance = [5 5 5 5 5 5];
% noise = [2 2 2 2 2 2];
% reliability = [0.8 0.8 0.8 0.8 0.8 0.8];
% offset = floor(rand(1,6)*10080);
% 
% for i=2:6
%     [s1,s2] = evalautocorr(period(1:i),reliability(1:i),variance(1:i),noise(1:i),10,offset(1:i))
% end

%Periodic offset:
period = [10080 10080 10080 10080 10080];
variance = [120 120 120 120 120];
noise = [1 1 1 1 1];
reliability = [0.8 0.8 0.8 0.8 0.8];
offset = 0:770:5*1440;
[finded_period,habit,y] = scenario(period,reliability,variance,noise,5,offset);
for i=1 : length(finded_period)
    figure
    plot(gethabit(habit{i},finded_period(i)))    
    title(strcat('superimpose period: ',num2str(period(i))))

end
for i=1 : length(finded_period)
    if finded_period(i) > 0
        Sfilter(y,finded_period(i))
    end
end

% 
% for i=2:10
%     [a1,a2] = evalautocorr(period(1:i),reliability(1:i),variance(1:i),noise(1:i),10,offset(1:i))
% end
%habit with several period: => work well

% period = [1440 2*1440 3*1440];
% variance = [15 15 15];
% noise = [5 5 5];
% reliability = [0.9 0.9 0.9];
% offset = [0:1440:2*1440];
% [a1,a2] = evalautocorr(period,reliability,variance,noise,10,offset)

